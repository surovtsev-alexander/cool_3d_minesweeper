package com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult

import com.surovtsev.finitestatemachine.event.Event

sealed interface EventProcessingResult {
    class Error(
        @Suppress("UNUSED_PARAMETER") message: String,
    ) : EventProcessingResult

    class Ok(
        val newEventToPush: Event? = null,
    ) : EventProcessingResult

    object Restarted: EventProcessingResult
}
