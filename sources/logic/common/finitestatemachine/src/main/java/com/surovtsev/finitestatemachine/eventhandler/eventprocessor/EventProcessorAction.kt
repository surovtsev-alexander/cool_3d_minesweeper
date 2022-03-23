package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult


typealias EventProcessorAction = suspend () -> EventProcessingResult


fun EventProcessorAction?.toEventProcessor(
    priority: EventProcessorPriority = EventProcessorPriority.NORMAL
): EventProcessor? =
    this?.let {
        EventProcessor(it, priority)
    }

fun EventProcessorAction?.toNormalPriorityEventProcessor(): EventProcessor? =
    this?.toEventProcessor(EventProcessorPriority.NORMAL)

fun EventProcessorAction?.toLastPriorityEventProcessor(): EventProcessor? =
    this?.toEventProcessor(EventProcessorPriority.LAST)
