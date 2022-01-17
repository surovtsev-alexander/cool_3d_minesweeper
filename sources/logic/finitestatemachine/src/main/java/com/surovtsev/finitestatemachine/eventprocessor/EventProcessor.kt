package com.surovtsev.finitestatemachine.eventprocessor

import com.surovtsev.finitestatemachine.event.Event

interface EventProcessor<E: Event> {
    suspend fun processEvent(
        event: E
    ): EventProcessingResult<E>
}