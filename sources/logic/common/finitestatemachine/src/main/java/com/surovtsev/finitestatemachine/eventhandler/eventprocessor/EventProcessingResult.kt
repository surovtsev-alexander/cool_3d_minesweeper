package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

import com.surovtsev.finitestatemachine.event.Event

sealed interface EventProcessingResult {
    class Error(
        message: String,
    ) : EventProcessingResult

    class Ok(
        val newEventToPush: Event? = null,
    ) : EventProcessingResult
}
