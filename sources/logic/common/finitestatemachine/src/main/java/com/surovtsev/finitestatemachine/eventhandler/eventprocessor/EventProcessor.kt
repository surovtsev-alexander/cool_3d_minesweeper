package com.surovtsev.finitestatemachine.eventhandler.eventprocessor


data class EventProcessor(
    val action: EventProcessorAction,
    val priority: EventProcessorPriority = EventProcessorPriority.NORMAL
)
