package com.surovtsev.rankingscreen.rankinscreenviewmodel.alt

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessor

class TemplateScreenViewModelEventProcessor<E: EventToViewModelAlt>(
    private val screenEventProcessor: EventProcessor<E>,
    private val finishActionHolder: FinishActionHolder,
): EventProcessor<E> {

    override suspend fun processEvent(
        event: E
    ): EventProcessingResult<E> {
        return screenEventProcessor.processEvent(
            event
        )
    }
}