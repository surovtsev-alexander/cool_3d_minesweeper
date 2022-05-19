package com.surovtsev.templateviewmodel.finitestatemachine.eventhandler

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toLastPriorityEventProcessor
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toNormalPriorityEventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.InitializationIsNotFinished
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.CloseErrorEvent
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.templateviewmodel.helpers.finishactionholder.FinishActionHolder

class TemplateViewModelEventHandler(
    private val stateHolder: StateHolder,
    private val finishActionHolder: FinishActionHolder,
    ): EventHandler {

    override val transitions: List<EventHandler.Transition> = emptyList()

    override fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult {
        val screenData = state.data

        do {
            if (state.description !is Description.Error) {
                break
            }

            if (event !is CloseErrorEvent) {
                return EventHandlingResult.Skip
            }

            if (event is EventToViewModel.CloseErrorAndFinish) {
                break
            }

            if (screenData is InitializationIsNotFinished) {
                return EventHandlingResult.ChangeWith(EventToViewModel.CloseErrorAndFinish)
            }
        } while(false)

        val eventProcessor = when(event) {
            is EventToViewModel.CloseError          -> ::closeError
                .toNormalPriorityEventProcessor()
            is EventToViewModel.CloseErrorAndFinish -> ::closeErrorAndFinish
                .toNormalPriorityEventProcessor()
            is EventToViewModel.Finish              -> ::finish
                .toNormalPriorityEventProcessor()
            is EventToViewModel.HandleScreenLeaving -> suspend { handleScreenLeaving(event.owner) }
                .toLastPriorityEventProcessor()
            else                                    -> null
        }

        return EventHandlingResult
            .GeneratorHelper
            .processOrSkipIfNull(
                eventProcessor
            )
    }

    private suspend fun handleScreenLeaving(
        @Suppress("UNUSED_PARAMETER") owner: LifecycleOwner
    ): EventProcessingResult {
        return EventProcessingResult.Ok(
            Event.TurnOff
        )
    }

    private suspend fun closeError(): EventProcessingResult {
        return closeErrorEventProcessingResult()
    }

    private suspend fun closeErrorAndFinish(): EventProcessingResult {
        invokeFinishAction()
        return closeErrorEventProcessingResult()
    }

    private suspend fun finish(): EventProcessingResult {
        invokeFinishAction()
        return EventProcessingResult.Ok()
    }

    private suspend fun closeErrorEventProcessingResult(
    ): EventProcessingResult {
        return EventProcessingResult.Ok(
            newState = stateHolder.toIdleState()
        )
    }

    private suspend fun invokeFinishAction() {
        finishActionHolder.finish()
    }
}