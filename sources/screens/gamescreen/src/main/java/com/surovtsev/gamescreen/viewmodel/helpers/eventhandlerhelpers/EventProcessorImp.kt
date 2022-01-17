package com.surovtsev.gamescreen.viewmodel.helpers.eventhandlerhelpers

import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.gamescreen.viewmodel.EventToGameScreenViewModel

class EventProcessorImp(
): EventProcessor<EventToGameScreenViewModel> {

    override suspend fun processEvent(event: EventToGameScreenViewModel): EventProcessingResult<EventToGameScreenViewModel> {
        return EventProcessingResult.Unprocessed()
    }
}