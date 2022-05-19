package com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.state.State

sealed interface EventProcessingResult {
    class Error(
        @Suppress("UNUSED_PARAMETER") message: String,
    ) : EventProcessingResult

    class Ok(
        val newEventToPush: Event? = null,
        val newState: State? = null,
    ) : EventProcessingResult
}
