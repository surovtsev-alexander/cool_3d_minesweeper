package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

import com.surovtsev.finitestatemachine.event.Event

sealed interface EventProcessingResult<E: Event> {
    class Error<E : Event>(
        message: String,
    ) : EventProcessingResult<E>

    class Ok<E : Event>(
        val newEventToPush: Event?,
    ) : EventProcessingResult<E>
}
