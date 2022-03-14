package com.surovtsev.core.viewmodel.finitestatemachine.eventhandler

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.core.viewmodel.helpers.FinishActionHolder
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.finitestatemachine.stateholder.StateHolder

class TemplateScreenViewModelEventHandler(
    private val closeErrorAndFinishEvent: Event,
    private val stateHolder: StateHolder,
    private val finishActionHolder: FinishActionHolder,
    private val noScreenData: Data,
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

            if (event !is EventToViewModel.CloseError) {
                return EventHandlingResult.Skip
            }

            if (event is EventToViewModel.CloseErrorAndFinish) {
                break
            }

            if (screenData is ScreenData.InitializationIsNotFinished) {
                return EventHandlingResult.ChangeWith(closeErrorAndFinishEvent)
            }
        } while(false)

        val eventProcessor = when(event) {
            is EventToViewModel.Finish              -> ::finish
            is EventToViewModel.CloseError          -> ::closeError
            is EventToViewModel.CloseErrorAndFinish -> ::closeErrorAndFinish
            is EventToViewModel.HandleScreenLeaving -> suspend { handleScreenLeaving(event.owner) }
            else                                    -> null
        }

        return EventHandlingResult
            .Helper
            .processOrSkipIfNull(
                eventProcessor
            )
    }

    private suspend fun handleScreenLeaving(
        owner: LifecycleOwner
    ): EventProcessingResult {
        stateHolder.publishIdleState(
            noScreenData
        )
        return EventProcessingResult.Ok()
    }

    private suspend fun closeError(): EventProcessingResult {
        stateHolder.publishIdleState()
        return EventProcessingResult.Ok()
    }

    private suspend fun closeErrorAndFinish(): EventProcessingResult {
        stateHolder.publishIdleState()
        finishActionHolder.finish()
        return EventProcessingResult.Ok()
    }

    private suspend fun finish(): EventProcessingResult {
        finishActionHolder.finish()
        return EventProcessingResult.Ok()
    }
}