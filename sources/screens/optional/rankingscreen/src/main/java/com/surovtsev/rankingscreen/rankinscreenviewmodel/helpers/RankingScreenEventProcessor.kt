package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenDataAlt
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.EventToRankingScreenViewModelAlt

class RankingScreenEventProcessor(
    private val stateHolder: StateHolder<RankingScreenDataAlt>,
) :
    EventProcessor<EventToRankingScreenViewModelAlt>
{
    override suspend fun processEvent(
        event: Event
    ): EventProcessingResult<EventToRankingScreenViewModelAlt> {
        val action = when (event) {
////            is EventToRankingScreenViewModelAlt.HandleScreenLeaving     -> suspend { handleScreenLeaving(event.owner) }
//            is EventToRankingScreenViewModelAlt.LoadData                -> ::loadData
//            is EventToRankingScreenViewModelAlt.FilterList              -> suspend { filterList(event.selectedSettingsId) }
//            is EventToRankingScreenViewModelAlt.SortListWithNoDelay     -> suspend { sortList(event.rankingTableSortParameters, false) }
//            is EventToRankingScreenViewModelAlt.SortList                -> suspend { sortList(event.rankingTableSortParameters, true) }
////            is EventToRankingScreenViewModelAlt.CloseError              -> ::closeError
            else                                                        -> null
        }


        return EventProcessingResult.Unprocessed<EventToRankingScreenViewModelAlt>()
    }


}
