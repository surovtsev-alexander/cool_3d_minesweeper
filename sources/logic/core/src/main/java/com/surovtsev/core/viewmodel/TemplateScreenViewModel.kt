package com.surovtsev.core.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.finitestatemachine.stateholder.StateHolderImp
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import logcat.logcat

typealias EventProcessor<E> = suspend () -> EventProcessingResult<E>

typealias FinishAction = () -> Unit


abstract class TemplateScreenViewModel<E: EventToViewModel, D: ScreenData>(
    override val mandatoryEvents: EventToViewModel.MandatoryEvents<E>,
    override val noScreenData: D,
    initialState: State<D>,
):
    ViewModel(),
    ErrorDialogPlacer<E, D>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl(),
    DefaultLifecycleObserver
{
    var finishAction: FinishAction? = null

    protected val stateHolder: StateHolder<D> = StateHolderImp(
        initialState,
        true
    )
    override val screenStateFlow: ScreenStateFlow<D>
        get() = stateHolder.state

    abstract suspend fun getEventProcessor(event: E): EventProcessor<E>?

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        handleEvent(
            mandatoryEvents.handleScreenLeavingEventFactory(owner)
        )
    }

    override fun handleEvent(event: E) {
        launchOnIOThread {
            logcat { "handleEvent: $event" }

            val currState = stateHolder.state.value

            val screenData = currState.data

            val isErrorState = currState.description is StateDescription.Error
            val isErrorDuringInitialization =
                isErrorState &&
                screenData is ScreenData.InitializationIsNotFinished


            val skipEventProcessing: Boolean

            if (isErrorState) {
                val isCloseErrorEvent = event is EventToViewModel.CloseError

                if (isCloseErrorEvent) {
                    if (isErrorDuringInitialization || event is EventToViewModel.CloseErrorAndFinish) {
                        closeError()
                        finish()
                        skipEventProcessing = true
                    } else {
                        skipEventProcessing = false
                    }
                } else {
                    skipEventProcessing = true
                }
            } else {
                skipEventProcessing = false
            }

            if (!skipEventProcessing) {
                if (event is EventToViewModel.Finish) {
                    finish()
                } else {
                    processEvent(event)
                }
            }
        }
    }

    private suspend fun finish() {
        finishAction?.let {
            withUIContext {
                it.invoke()
            }
        }
    }

    private suspend fun processEvent(event: E) {
        val eventProcessor = getEventProcessor(event)

        if (eventProcessor == null) {
            stateHolder.publishErrorState("unable to process internal event")
        } else {
            if (event.setLoadingStateBeforeProcessing) {
                stateHolder.publishLoadingState()
            }
            val eventProcessingResult = eventProcessor.invoke()

            if (eventProcessingResult is EventProcessingResult.PushNewEvent<E>) {
                return handleEvent(
                    eventProcessingResult.event
                )
            }
        }
    }

    protected open suspend fun handleScreenLeaving(
        owner: LifecycleOwner
    ): EventProcessingResult<E> {
        stateHolder.publishIdleState(
            noScreenData
        )
        return EventProcessingResult.Processed()
    }

    protected suspend fun closeError(): EventProcessingResult<E> {
        stateHolder.publishIdleState()
        return EventProcessingResult.Processed()
    }

//    protected inline fun <reified T: D> processIfDataNullable(
//        checkData: (screenData: T?) -> Boolean,
//        errorAction: (screenData: D?) -> Unit,
//        action: (screenData: T?) -> Unit
//    ) {
//        val screenData = dataHolder.value?.screenData
//        val castedScreenData = screenData as? T
//
//        if (castedScreenData == null || !checkData(castedScreenData)) {
//            errorAction(screenData)
//        } else {
//            action(castedScreenData)
//        }
//    }
//
//    protected inline fun <reified T: D>processIfData(
//        checkData: (screenData: T) -> Boolean,
//        errorAction: () -> Unit,
//        action: (screenData: T) -> Unit
//    ) {
//        val screenData =  dataHolder.value?.screenData
//        val castedScreenData = screenData as? T
//
//        if (castedScreenData == null || !checkData(castedScreenData)) {
//            errorAction()
//        } else {
//            action(castedScreenData)
//        }
//    }

    protected suspend inline fun <reified T: D> doActionIfStateIsChildIs(
        errorMessage: String, action: (screenData: T) -> Unit
    ) {
        val screenData = stateHolder.state.value.data

        if (screenData !is T) {
            stateHolder.publishErrorState(errorMessage)
        } else {
            action.invoke(screenData)
        }
    }

}