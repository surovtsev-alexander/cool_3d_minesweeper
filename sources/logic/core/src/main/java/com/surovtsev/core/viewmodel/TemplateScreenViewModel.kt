package com.surovtsev.core.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.finitestatemachine.helpers.concrete.FSMState
import com.surovtsev.finitestatemachine.helpers.concrete.FSMStateHolder
import com.surovtsev.finitestatemachine.helpers.concrete.FSMStateHolderImp
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import logcat.logcat

typealias EventProcessor = suspend () -> Unit

typealias FinishAction = () -> Unit


abstract class TemplateScreenViewModel<E: EventToViewModel, D: ScreenData>(
    override val mandatoryEvents: EventToViewModel.MandatoryEvents<E>,
    override val noScreenData: D,
    initialState: FSMState<D>,
):
    ViewModel(),
    ErrorDialogPlacer<E, D>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl(),
    DefaultLifecycleObserver
{
    var finishAction: FinishAction? = null

    protected val fsmStateHolder: FSMStateHolder<D> = FSMStateHolderImp(
        initialState,
        true
    )
    override val screenStateFlow: ScreenStateFlow<D>
        get() = fsmStateHolder.state

    abstract suspend fun getEventProcessor(event: E): EventProcessor?

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        handleEvent(
            mandatoryEvents.handleScreenLeavingEventFactory(owner)
        )
    }

    override fun handleEvent(event: E) {
        launchOnIOThread {
            logcat { "handleEvent: $event" }

            val currState = fsmStateHolder.state.value

            val screenData = currState.data

            val isErrorState = currState.state is State.Error
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
            fsmStateHolder.publishErrorState("unable to process internal event")
        } else {
            if (event.setLoadingStateBeforeProcessing) {
                fsmStateHolder.publishLoadingState()
            }
            eventProcessor.invoke()
        }
    }

    protected open suspend fun handleScreenLeaving(
        owner: LifecycleOwner
    ) {
        fsmStateHolder.publishIdleState(
            noScreenData
        )
    }

    protected suspend fun closeError() {
        fsmStateHolder.publishIdleState()
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
        val screenData = fsmStateHolder.state.value.data

        if (screenData !is T) {
            fsmStateHolder.publishErrorState(errorMessage)
        } else {
            action.invoke(screenData)
        }
    }

}