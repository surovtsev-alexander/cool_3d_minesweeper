package com.surovtsev.settingsscreen.viewmodel.helpers

import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.settingsscreen.viewmodel.EventToSettingsScreenViewModel

class EventProcessorImp: EventProcessor<EventToSettingsScreenViewModel> {

    override suspend fun processEvent(event: EventToSettingsScreenViewModel): EventProcessingResult<EventToSettingsScreenViewModel> {
        return EventProcessingResult.Unprocessed()
    }
}