package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.core.helpers.sorting.DefaultRankingTableSortParameters
import com.surovtsev.core.helpers.sorting.DefaultSortDirectionForSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toNormalPriorityEventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.toError
import com.surovtsev.finitestatemachine.state.toIdle
import com.surovtsev.rankingscreen.dagger.RankingScreenScope
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.EventToRankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.RankingScreenData
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.delay
import logcat.logcat
import javax.inject.Inject

@RankingScreenScope
class EventHandlerImp @Inject constructor(
    private val eventHandlerParameters: EventHandlerParameters,
): EventHandler {

    override val transitions: List<EventHandler.Transition> = emptyList()

    override fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult {
        val eventProcessorAction = when (event) {
            is EventToViewModel.Init                                 -> ::loadData
            is EventToRankingScreenViewModel.FilterList              -> suspend { filterList(event.selectedSettingsId) }
            is EventToRankingScreenViewModel.SortListWithNoDelay     -> suspend { sortList(event.rankingTableSortParameters, false) }
            is EventToRankingScreenViewModel.SortList                -> suspend { sortList(event.rankingTableSortParameters, true) }
            else                                                     -> null
        }

        return EventHandlingResult.GeneratorHelper.processOrSkipIfNull(
            eventProcessorAction.toNormalPriorityEventProcessor()
        )
    }

    companion object {
        const val MINIMAL_UI_ACTION_DELAY = 0L
    }

    object ErrorMessages {
        val errorWhileFilteringRankingListFactory = { code: Int -> "error (code: $code) while filtering ranking list" }
        val errorWhileSortingListFactory = { code: Int -> "error (code: $code) while sorting list" }
    }

    private suspend fun loadData(
    ): EventProcessingResult {
        val currTimeSpanComponent: TimeSpanComponent =
            eventHandlerParameters
                .timeSpanComponent

        val settingsDao = eventHandlerParameters.settingsDao
        val rankingDao = eventHandlerParameters.rankingDao
        val saveController = eventHandlerParameters.saveController

        val settingsListIsLoaded = doActionWithDelayUpToDefaultMinimal(currTimeSpanComponent.asyncTimeSpan) {
            val settingsList = settingsDao.getAll()
            val winsCountMap = rankingDao.getWinsCountMap()
            RankingScreenData.SettingsListIsLoaded(
                settingsList,
                winsCountMap
            )
        }

        val newState = eventHandlerParameters.fsmStateFlow.value.toIdle(
            settingsListIsLoaded
        )

        return settingsDao.getBySettingsData(
            saveController.loadSettingDataOrDefault()
        ).let {
            if (it != null) {
                EventProcessingResult.Ok(
                    EventToRankingScreenViewModel.FilterList(it.id),
                    newState = newState
                )
            } else {
                EventProcessingResult.Ok(
                    newState = newState
                )
            }
        }
    }

    private suspend fun filterList(
        selectedSettingsId: Long
    ): EventProcessingResult {

        val rankingListWithPlaces =
            eventHandlerParameters.rankingListHelper
                .createRankingListWithPlaces(
                    selectedSettingsId
                )

        val state = eventHandlerParameters.fsmStateFlow.value
        val rankingScreenData = state.data

        val newState = if (rankingScreenData !is RankingScreenData.SettingsListIsLoaded) {
            // TODO: fixme
            state.toError(
                ErrorMessages.errorWhileFilteringRankingListFactory(2)
            )
        } else {
            // Do not set state to IDLE if you want to avoid blinking loading ui attributes.
            state.toIdle(
                RankingScreenData.RankingListIsPrepared(
                    rankingScreenData,
                    selectedSettingsId,
                    rankingListWithPlaces,
                )
            )
        }

        return EventProcessingResult.Ok(
            EventToRankingScreenViewModel.SortList(
                DefaultRankingTableSortParameters
            ),
            newState = newState
        )
    }

    private suspend fun sortList(
        rankingTableSortParameters: RankingTableSortParameters,
        doDelay: Boolean
    ): EventProcessingResult {
        val currTimeSpanComponent = eventHandlerParameters.timeSpanComponent

        val state = eventHandlerParameters.fsmStateFlow.value
        val rankingScreenData = state.data

        if (rankingScreenData !is RankingScreenData.RankingListIsPrepared) {
            // TODO: fixme
            val newState = state.toError(
                ErrorMessages.errorWhileFilteringRankingListFactory(1)
            )

            return EventProcessingResult.Ok(
                newState = newState
            )
        }

        val filteredRankingList = rankingScreenData.rankingListWithPlaces

        logcat { "rankingTableSortType: $rankingTableSortParameters" }

        val sortingAction = {
            eventHandlerParameters.rankingListHelper.sortData(
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

        val newState = state.toIdle(
            RankingScreenData.RankingListIsSorted(
                rankingScreenData,
                rankingTableSortParameters,
                sortedData,
                directionOfSortableColumns,
            )
        )
        return EventProcessingResult.Ok(
            newState = newState
        )
    }

    private suspend fun<T> doActionWithDelayUpToDefaultMinimal(
        asyncTimeSpan: AsyncTimeSpan,
        block: () -> T
    ): T {
        asyncTimeSpan.flush()
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
