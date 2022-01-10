package com.surovtsev.core.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import logcat.logcat

typealias CommandProcessor = suspend () -> Unit

typealias FinishAction = () -> Unit

typealias ScreenStateFlow<T> = StateFlow<ScreenState<out T>>

abstract class TemplateScreenViewModel<C: EventToViewModel, D: ScreenData>(
    override val mandatoryEvents: EventToViewModel.MandatoryEvents<C>,
    override val noScreenData: D,
    initialState: ScreenState<out D>,
):
    ViewModel(),
    ErrorDialogPlacer<C, D>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl(),
    DefaultLifecycleObserver
{
    var finishAction: FinishAction? = null

    private val _state: MutableStateFlow<ScreenState<out D>> = MutableStateFlow(initialState)
    override val state: ScreenStateFlow<D> = _state.asStateFlow()

    abstract suspend fun getCommandProcessor(command: C): CommandProcessor?

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        handleCommand(
            mandatoryEvents.handleScreenLeavingEventFactory(owner)
        )
    }

    override fun handleCommand(command: C) {
        launchOnIOThread {
            logcat { "handleCommand: $command" }

            val currState = state.value

            val screenData = currState.screenData

            val isErrorState = currState is ScreenState.Error
            val isErrorDuringInitialization =
                isErrorState &&
                screenData is ScreenData.InitializationIsNotFinished


            val skipProcessingCommand: Boolean

            if (isErrorState) {
                val isCloseErrorCommand = command is EventToViewModel.CloseError

                if (isCloseErrorCommand) {
                    if (isErrorDuringInitialization || command is EventToViewModel.CloseErrorAndFinish) {
                        closeError()
                        finish()
                        skipProcessingCommand = true
                    } else {
                        skipProcessingCommand = false
                    }
                } else {
                    skipProcessingCommand = true
                }
            } else {
                skipProcessingCommand = false
            }

            if (!skipProcessingCommand) {
                if (command is EventToViewModel.Finish) {
                    finish()
                } else {
                    tryToProcessCommand(command)
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

    private suspend fun tryToProcessCommand(command: C) {
        val commandProcessor = getCommandProcessor(command)

        if (commandProcessor == null) {
            publishErrorState("unable to process internal command")
        } else {
            if (command.setLoadingStateWhileProcessing) {
                publishLoadingState()
            }
            commandProcessor.invoke()
        }
    }

    protected open suspend fun handleScreenLeaving(
        owner: LifecycleOwner
    ) {
        publishIdleState(
            noScreenData
        )
    }

    protected suspend fun publishLoadingState(
        screenData: D = getCurrentScreenData()
    ) {
        publishNewState(
            ScreenState.Loading(
                screenData
            )
        )
    }

    protected suspend fun publishErrorState(
        message: String,
        screenData: D = getCurrentScreenData()
    ) {
        publishNewState(
            ScreenState.Error(
                screenData,
                message
            )
        )
    }

    protected suspend fun publishIdleState(
        screenData: D
    ) {
        publishNewState(
            ScreenState.Idle(
                screenData
            )
        )
    }

    private suspend fun publishNewState(
        screenState: ScreenState<out D>
    ) {
        withUIContext {
            _state.value = screenState
        }
    }

    protected suspend fun closeError() {
        publishIdleState(
            getCurrentScreenData()
        )
    }

    private fun getCurrentScreenData(): D {
        return state.value.screenData
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
        val screenData = state.value.screenData

        if (screenData !is T) {
            publishErrorState(errorMessage)
        } else {
            action.invoke(screenData)
        }
    }

}