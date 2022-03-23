package com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.eventhandler

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.eventtoviewmodel.CloseErrorEvent
import com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.templatescreenviewmodel.helpers.finishactionholder.FinishActionHolder
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

class TemplateScreenViewModelEventHandler(
    private val stateHolder: StateHolder,
    private val finishActionHolder: FinishActionHolder,
    ): EventHandler {

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
        stateHolder.publishDefaultInitialState()

        return EventProcessingResult.Ok()
    }

    private suspend fun closeError(): EventProcessingResult {
        closeErrorAction()
        return EventProcessingResult.Ok()
    }

    private suspend fun closeErrorAndFinish(): EventProcessingResult {
        closeErrorAction()
        invokeFinishAction()
        return EventProcessingResult.Ok()
    }

    private suspend fun finish(): EventProcessingResult {
        invokeFinishAction()
        return EventProcessingResult.Ok()
    }

    private suspend fun closeErrorAction() {
        stateHolder.publishIdleState()
    }

    private suspend fun invokeFinishAction() {
        finishActionHolder.finish()
    }
}