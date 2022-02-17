package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

import com.surovtsev.finitestatemachine.event.Event

interface EventProcessor<E: Event> {
    fun processEvent(
        e: Event
    ): EventProcessingResult<E>
}
