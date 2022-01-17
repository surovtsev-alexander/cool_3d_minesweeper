package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

import com.surovtsev.finitestatemachine.event.Event

sealed interface EventProcessingResult<E: Event> {
    class Unprocessed<E: Event>: EventProcessingResult<E>

    class Processed<E: Event>: EventProcessingResult<E>

    class PushNewEvent<E: Event>(
        val event: E
    ): EventProcessingResult<E>
}