package com.surovtsev.core.viewmodel.finitestatemachine.eventhandler

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.core.viewmodel.helpers.FinishActionHolder
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.stateholder.StateHolder

class TemplateScreenViewModelEventHandler<E: EventToViewModel, D: ScreenData>(
    private val closeErrorAndFinishEvent: E,
    private val stateHolder: StateHolder<D>,
    private val finishActionHolder: FinishActionHolder,
    private val noScreenData: D,
    ): EventHandler<E, D> {

    override fun handleEvent(
        event: E,
        state: State<D>
    ): EventHandlingResult<E> {
        val screenData = state.data

        do {
            if (state.description !is StateDescription.Error) {
                break
            }

            if (event !is EventToViewModel.CloseError) {
                return EventHandlingResult.Skip()
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
    ): EventProcessingResult<E> {
        stateHolder.publishIdleState(
            noScreenData
        )
        return EventProcessingResult.Ok()
    }

    private suspend fun closeError(): EventProcessingResult<E> {
        stateHolder.publishIdleState()
        return EventProcessingResult.Ok()
    }

    private suspend fun closeErrorAndFinish(): EventProcessingResult<E> {
        stateHolder.publishIdleState()
        finishActionHolder.finish()
        return EventProcessingResult.Ok()
    }

    private suspend fun finish(): EventProcessingResult<E> {
        finishActionHolder.finish()
        return EventProcessingResult.Ok()
    }
}