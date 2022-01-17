package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.eventhandlerhelpers

import com.surovtsev.core.helpers.sorting.DefaultRankingTableSortParameters
import com.surovtsev.core.helpers.sorting.DefaultSortDirectionForSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.rankingscreen.dagger.RankingScreenScope
import com.surovtsev.rankingscreen.rankinscreenviewmodel.EventToRankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenData
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.delay
import logcat.logcat
import javax.inject.Inject

@RankingScreenScope
class EventProcessorImp @Inject constructor(
    private val eventProcessorParameters: EventProcessorParameters,
): EventProcessor<EventToRankingScreenViewModel>
{
    companion object {
        const val MINIMAL_UI_ACTION_DELAY = 3000L
    }

    object ErrorMessages {
        val errorWhileFilteringRankingListFactory = { code: Int -> "error (code: $code) while filtering ranking list" }
        val errorWhileSortingListFactory = { code: Int -> "error (code: $code) while sorting list" }
    }

    override suspend fun processEvent(
        event: EventToRankingScreenViewModel
    ): EventProcessingResult<EventToRankingScreenViewModel> {
        val eventProcessor = when (event) {
            is EventToRankingScreenViewModel.LoadData                -> ::loadData
            is EventToRankingScreenViewModel.FilterList              -> suspend { filterList(event.selectedSettingsId) }
            is EventToRankingScreenViewModel.SortListWithNoDelay     -> suspend { sortList(event.rankingTableSortParameters, false) }
            is EventToRankingScreenViewModel.SortList                -> suspend { sortList(event.rankingTableSortParameters, true) }
            else                                                     -> null
        }

        return if (eventProcessor == null) {
            EventProcessingResult.Unprocessed()
        } else {
            eventProcessor()
        }
    }

    private suspend fun loadData(
    ): EventProcessingResult<EventToRankingScreenViewModel> {

        eventProcessorParameters
            .restartableCoroutineScopeComponent
            .subscriberImp
            .restart()

        val currTimeSpanComponent: TimeSpanComponent =
            eventProcessorParameters
                .timeSpanComponent

        val settingsDao = eventProcessorParameters.settingsDao
        val rankingDao = eventProcessorParameters.rankingDao
        val saveController = eventProcessorParameters.saveController

        val settingsListIsLoaded = doActionWithDelayUpToDefaultMinimal(currTimeSpanComponent.asyncTimeSpan) {
            val settingsList = settingsDao.getAll()
            val winsCountMap = rankingDao.getWinsCountMap()
            RankingScreenData.SettingsListIsLoaded(
                settingsList,
                winsCountMap
            )
        }

        currTimeSpanComponent
            .asyncTimeSpan
            .flush()

        eventProcessorParameters.stateHolder.publishIdleState(
            settingsListIsLoaded
        )

        return settingsDao.getBySettingsData(
            saveController.loadSettingDataOrDefault()
        ).let {
            if (it != null) {
                EventProcessingResult.PushNewEvent(
                    EventToRankingScreenViewModel.FilterList(it.id)
                )
            } else {
                EventProcessingResult.Processed()
            }
        }
    }

    private suspend fun filterList(
        selectedSettingsId: Long
    ): EventProcessingResult<EventToRankingScreenViewModel> {

        val rankingListWithPlaces =
            eventProcessorParameters.rankingListHelper
                .createRankingListWithPlaces(
                    selectedSettingsId
                )

        val stateHolder = eventProcessorParameters.stateHolder
        val rankingScreenData = stateHolder.getCurrentData()

        if (rankingScreenData !is RankingScreenData.SettingsListIsLoaded) {
            stateHolder.publishErrorState(
                ErrorMessages.errorWhileFilteringRankingListFactory(2)
            )
        } else {
            // Do not set state to IDLE in order to avoid blinking loading ui attributes.
            stateHolder.publishIdleState(
                RankingScreenData.RankingListIsPrepared(
                    rankingScreenData,
                    selectedSettingsId,
                    rankingListWithPlaces
                )
            )
        }


        return EventProcessingResult.PushNewEvent(
            EventToRankingScreenViewModel.SortList(
                DefaultRankingTableSortParameters
            )
        )
    }

    private suspend fun sortList(
        rankingTableSortParameters: RankingTableSortParameters,
        doDelay: Boolean
    ): EventProcessingResult<EventToRankingScreenViewModel> {
        val currTimeSpanComponent = eventProcessorParameters.timeSpanComponent

        val stateHolder = eventProcessorParameters.stateHolder
        val rankingScreenData = stateHolder.state.value.data

        if (rankingScreenData !is RankingScreenData.RankingListIsPrepared) {
            stateHolder.publishErrorState(
                ErrorMessages.errorWhileSortingListFactory(1)
            )

            return EventProcessingResult.Processed()
        }

        val filteredRankingList = rankingScreenData.rankingListWithPlaces

        logcat { "rankingTableSortType: $rankingTableSortParameters" }

        val sortingAction = {
            eventProcessorParameters.rankingListHelper.sortData(
                filteredRankingList,
                rankingTableSortParameters
            )
        }

        val sortedData = if (doDelay) {
            doActionWithDelayUpToDefaultMinimal(currTimeSpanComponent.asyncTimeSpan, sortingAction)
        } else {
            sortingAction.invoke()
        }

        val directionOfSortableColumns =
            (if (rankingScreenData is RankingScreenData.RankingListIsSorted) {
                rankingScreenData.directionOfSortableColumns
            } else {
                DefaultSortDirectionForSortableColumns
            }).map { (k, v) ->
                k to if (k == rankingTableSortParameters.rankingTableColumn) {
                    rankingTableSortParameters.sortDirection
                } else {
                    v
                }
            }.toMap()

        stateHolder.publishIdleState(
            RankingScreenData.RankingListIsSorted(
                rankingScreenData,
                rankingTableSortParameters,
                sortedData,
                directionOfSortableColumns
            )
        )
        return EventProcessingResult.Processed()
    }

    private suspend fun<T> doActionWithDelayUpToDefaultMinimal(
        asyncTimeSpan: AsyncTimeSpan,
        block: () -> T
    ): T {
        asyncTimeSpan.turnOn()

        val res = block.invoke()

        asyncTimeSpan.turnOff()
        val timeToDelay = MINIMAL_UI_ACTION_DELAY - asyncTimeSpan.getElapsed()

        if (timeToDelay > 0) {
            delay(timeToDelay)
        }

        return res
    }
}
