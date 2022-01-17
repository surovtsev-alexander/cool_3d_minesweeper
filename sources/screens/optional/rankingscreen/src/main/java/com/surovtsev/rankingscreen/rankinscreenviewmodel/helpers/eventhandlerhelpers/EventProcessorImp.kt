package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.eventhandlerhelpers

import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.rankingscreen.rankinscreenviewmodel.EventToRankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenData
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.EventToRankingScreenViewModelAlt

class EventProcessorImp(
) :
    EventProcessor<EventToRankingScreenViewModel>
{
    override suspend fun processEvent(
        event: EventToRankingScreenViewModel
    ): EventProcessingResult<EventToRankingScreenViewModel> {

        return EventProcessingResult.Unprocessed()
    }
}
