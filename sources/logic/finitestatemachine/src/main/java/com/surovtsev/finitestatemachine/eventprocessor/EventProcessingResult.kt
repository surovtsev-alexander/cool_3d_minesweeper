package com.surovtsev.finitestatemachine.eventprocessor

import com.surovtsev.finitestatemachine.event.Event

sealed interface EventProcessingResult {
    object Unprocessed: EventProcessingResult

    object Processed: EventProcessingResult

    class PushNewEvent(
        event: Event
    ): EventProcessingResult
}