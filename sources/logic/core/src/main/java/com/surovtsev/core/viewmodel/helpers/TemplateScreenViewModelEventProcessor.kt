package com.surovtsev.core.viewmodel.helpers

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.stateholder.StateHolder

class TemplateScreenViewModelEventProcessor<E: EventToViewModel, D: ScreenData>(
    private val stateHolder: StateHolder<D>,
    private val finishActionHolder: FinishActionHolder,
    private val noScreenData: D,
): EventProcessor<E> {
    override suspend fun processEvent(event: E): EventProcessingResult<E> {
        val eventProcessor = when(event) {
            is EventToViewModel.Finish              -> ::finish
            is EventToViewModel.CloseError          -> ::closeError
            is EventToViewModel.CloseErrorAndFinish -> ::closeErrorAndFinish
            is EventToViewModel.HandleScreenLeaving -> suspend { handleScreenLeaving(event.owner) }
            else                                    -> null
        }

        return if (eventProcessor == null) {
            EventProcessingResult.Unprocessed()
        } else {
            eventProcessor()
        }
    }

    public suspend fun handleScreenLeaving(
        owner: LifecycleOwner
    ): EventProcessingResult<E> {
        stateHolder.publishIdleState(
            noScreenData
        )
        return EventProcessingResult.Processed()
    }

    public suspend fun closeError(): EventProcessingResult<E> {
        stateHolder.publishIdleState()
        return EventProcessingResult.Processed()
    }

    public suspend fun closeErrorAndFinish(): EventProcessingResult<E> {
        stateHolder.publishIdleState()
        finishActionHolder.finish()
        return EventProcessingResult.Processed()
    }

    public suspend fun finish(): EventProcessingResult<E> {
        finishActionHolder.finish()
        return EventProcessingResult.Processed()
    }
}