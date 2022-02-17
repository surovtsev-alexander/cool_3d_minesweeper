package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor

sealed interface EventHandlingResult<E: Event> {
    class RaiseError<E: Event>(
        val message: String
    ): EventHandlingResult<E>

    class Process<E: Event>(
        eventProcessor: EventProcessor<E>
    ): EventHandlingResult<E>

    class Skip<E: Event>: EventHandlingResult<E>

    class ChangeWith<E: Event>(
        val event: E,
    ): EventHandlingResult<E>
}
