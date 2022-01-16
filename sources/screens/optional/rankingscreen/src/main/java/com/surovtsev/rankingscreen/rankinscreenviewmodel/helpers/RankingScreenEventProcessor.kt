package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers

import com.surovtsev.core.helpers.sorting.DefaultRankingTableSortParameters
import com.surovtsev.core.helpers.sorting.DefaultSortDirectionForSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.helpers.concrete.StateHolder
import com.surovtsev.rankingscreen.dagger.DaggerRankingComponent
import com.surovtsev.rankingscreen.dagger.RankingComponent
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenDataAlt
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenViewModelAlt
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.EventToRankingScreenViewModelAlt
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.delay
import logcat.logcat

class RankingScreenEventProcessor<E: EventToRankingScreenViewModelAlt, D: RankingScreenDataAlt>(
    private val rankingScreenDaggerComponentsHolder: RankingScreenDaggerComponentsHolder,
    private val stateHolder: StateHolder<D>,
)
    : com.surovtsev.finitestatemachine.eventprocessor.EventProcessor<E>
{
    override suspend fun processEvent(
        event: Event
    ): EventProcessingResult {
        val action = when (event) {
//            is EventToRankingScreenViewModelAlt.HandleScreenLeaving     -> suspend { handleScreenLeaving(event.owner) }
            is EventToRankingScreenViewModelAlt.LoadData                -> ::loadData
            is EventToRankingScreenViewModelAlt.FilterList              -> suspend { filterList(event.selectedSettingsId) }
            is EventToRankingScreenViewModelAlt.SortListWithNoDelay     -> suspend { sortList(event.rankingTableSortParameters, false) }
            is EventToRankingScreenViewModelAlt.SortList                -> suspend { sortList(event.rankingTableSortParameters, true) }
//            is EventToRankingScreenViewModelAlt.CloseError              -> ::closeError
            else                                                        -> null
        }

        return if (action == null) {
            EventProcessingResult.Unprocessed
        } else {
            return action.invoke()
        }
    }

    private suspend fun loadData(): EventProcessingResult {
        val currRestartableCoroutineScopeComponent: RestartableCoroutineScopeComponent
        rankingScreenDaggerComponentsHolder.restartableCoroutineScopeComponent.let {
            currRestartableCoroutineScopeComponent =
                it?.also {
                    it.subscriberImp.restart()
                } ?: DaggerRestartableCoroutineScopeComponent.create()
                    .also {
                        rankingScreenDaggerComponentsHolder.restartableCoroutineScopeComponent = it
                    }
        }

        val currTimeSpanComponent: TimeSpanComponent

        rankingScreenDaggerComponentsHolder.timeSpanComponent.let {
            currTimeSpanComponent =
                it
                    ?: DaggerTimeSpanComponent
                        .builder()
                        .subscriptionsHolderEntryPoint(
                            SubscriptionsHolderComponentFactoryHolderImp.create(
                                currRestartableCoroutineScopeComponent,
                                "RankingScreenViewModel:TimeSpanComponent"
                            )
                        )
                        .build()
                        .apply {
                            rankingScreenDaggerComponentsHolder.timeSpanComponent = this
                        }
        }

        val currRankingComponent: RankingComponent

        rankingScreenDaggerComponentsHolder.rankingComponent.let {
            currRankingComponent =
                it ?: DaggerRankingComponent
                    .builder()
                    .appComponentEntryPoint(rankingScreenDaggerComponentsHolder.appComponentEntryPoint)
                    .timeSpanComponentEntryPoint(currTimeSpanComponent)
                    .build()
                    .apply {
                        rankingScreenDaggerComponentsHolder.rankingComponent = this
                    }
        }

        val settingsDao = currRankingComponent.settingsDao
        val rankingDao = currRankingComponent.rankingDao
        val saveController = currRankingComponent.saveController

        val settingsListIsLoaded = doActionWithDelayUpToDefaultMinimal(currTimeSpanComponent.asyncTimeSpan) {
            val settingsList = settingsDao.getAll()
            val winsCountMap = rankingDao.getWinsCountMap()
            RankingScreenDataAlt.SettingsListIsLoaded(
                settingsList,
                winsCountMap
            )
        }

        currTimeSpanComponent
            .asyncTimeSpan
            .flush()

        stateHolder.publishIdleState(
            settingsListIsLoaded as D
        )

        settingsDao.getBySettingsData(
            saveController.loadSettingDataOrDefault()
        )?.let {
            return EventProcessingResult.PushNewEvent(
                EventToRankingScreenViewModelAlt.FilterList(it.id)
            )
        }
        return EventProcessingResult.Processed
    }

    private suspend fun filterList(
        selectedSettingsId: Long
    ): EventProcessingResult {
        val currRankingComponent = rankingScreenDaggerComponentsHolder.rankingComponent

        if (currRankingComponent == null) {
            stateHolder.publishErrorState(
                RankingScreenViewModelAlt.ErrorMessages.errorWhileFilteringRankingListFactory(1)
            )
            return EventProcessingResult.Processed
        }

        val rankingListWithPlaces =
            currRankingComponent.rankingListHelper
                .createRankingListWithPlaces(
                    selectedSettingsId
                )

        val rankingScreenData = stateHolder.state.value.data

        if (rankingScreenData !is RankingScreenDataAlt.SettingsListIsLoaded) {
            stateHolder.publishErrorState(
                RankingScreenViewModelAlt.ErrorMessages.errorWhileFilteringRankingListFactory(2)
            )
        } else {
            // Do not set state to IDLE in order to avoid blinking loading ui attributes.
            stateHolder.publishIdleState(
                RankingScreenDataAlt.RankingListIsPrepared(
                    rankingScreenData,
                    selectedSettingsId,
                    rankingListWithPlaces
                ) as D
            )
        }


        return EventProcessingResult.PushNewEvent(
            EventToRankingScreenViewModelAlt.SortList(
                DefaultRankingTableSortParameters
            )
        )
    }

    private suspend fun sortList(
        rankingTableSortParameters: RankingTableSortParameters,
        doDelay: Boolean
    ): EventProcessingResult {
        val currTimeSpanComponent = rankingScreenDaggerComponentsHolder.timeSpanComponent

        if (currTimeSpanComponent == null) {
            stateHolder.publishErrorState(
                RankingScreenViewModelAlt.ErrorMessages.errorWhileSortingListFactory(1)
            )

            return EventProcessingResult.Processed
        }

        val currRankingComponent = rankingScreenDaggerComponentsHolder.rankingComponent

        if (currRankingComponent == null) {
            stateHolder.publishErrorState(
                RankingScreenViewModelAlt.ErrorMessages.errorWhileSortingListFactory(2)
            )

            return EventProcessingResult.Processed
        }

        val rankingScreenData = stateHolder.getCurrentData()

        if (rankingScreenData !is RankingScreenDataAlt.RankingListIsPrepared) {
            stateHolder.publishErrorState(
                RankingScreenViewModelAlt.ErrorMessages.errorWhileSortingListFactory(3)
            )

            return EventProcessingResult.Processed
        }

        val filteredRankingList = rankingScreenData.rankingListWithPlaces

        logcat { "rankingTableSortType: $rankingTableSortParameters" }

        val sortingAction = {
            currRankingComponent.rankingListHelper.sortData(
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
            (if (rankingScreenData is RankingScreenDataAlt.RankingListIsSorted) {
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
            RankingScreenDataAlt.RankingListIsSorted(
                rankingScreenData,
                rankingTableSortParameters,
                sortedData,
                directionOfSortableColumns
            ) as D
        )
        return EventProcessingResult.Processed
    }

    companion object {
        const val MINIMAL_UI_ACTION_DELAY = 3000L
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
