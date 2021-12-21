package com.surovtsev.core.viewmodel

import androidx.lifecycle.*
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import logcat.logcat

typealias CommandProcessor = suspend () -> Unit

typealias FinishAction = () -> Unit

typealias ScreenStateValue<T> = LiveData<ScreenState<out T>>

typealias HandleScreenLeavingCommandFactory<C> = (owner: LifecycleOwner) -> C

abstract class TemplateScreenViewModel<C: CommandFromScreen, D: ScreenData>(
    private val initCommand: C,
    private val handleScreenLeavingCommandFactory: HandleScreenLeavingCommandFactory<C>,
    private val noScreenData: D,
    initialState: MutableLiveData<ScreenState<out D>>,
):
    ViewModel(),
    ScreenCommandHandler<C>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl(),
    DefaultLifecycleObserver
{
    var finishAction: FinishAction? = null

    private val _state: MutableLiveData<ScreenState<out D>> = initialState
    val state: LiveData<ScreenState<out D>> = _state

    abstract suspend fun getCommandProcessor(command: C): CommandProcessor?

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        handleCommand(
            handleScreenLeavingCommandFactory(owner)
        )
    }

    override fun handleCommand(command: C) {
        launchOnIOThread {
            logcat { "handleCommand: $command" }

            val currState = state.value

            val screenData = currState?.screenData

            val isErrorState =
                currState != null &&
                        currState is ScreenState.Error
            val isErrorDuringInitialization =
                isErrorState &&
                        screenData != null &&
                        screenData is ScreenData.InitializationIsNotFinished


            if (isErrorState) {
                val isCloseErrorCommand = command is CommandFromScreen.CloseError

                if (isCloseErrorCommand) {
                    if (isErrorDuringInitialization || command is CommandFromScreen.CloseErrorAndFinish) {
                        finishAction?.let {
                            closeError()
                            withUIContext {
                                it.invoke()
                            }
                        }
                    } else {
                        tryToProcessCommand(command)
                    }
                }
            } else {
                tryToProcessCommand(command)
            }
        }
    }

    private suspend fun tryToProcessCommand(command: C) {
        val commandProcessor = getCommandProcessor(command)

        if (commandProcessor == null) {
            publishErrorState("unable to process internal command")
        } else {
            publishLoadingState()
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
        screenData: D = getCurrentScreenDataOrNoData()
    ) {
        publishNewState(
            ScreenState.Loading(
                screenData
            )
        )
    }

    protected suspend fun publishErrorState(
        message: String,
        screenData: D = getCurrentScreenDataOrNoData()
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
            getCurrentScreenDataOrNoData()
        )
    }

    protected fun getCurrentScreenDataOrNoData(): D {
        return state.value?.screenData ?: noScreenData
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
        val screenData = state.value?.screenData

        if (screenData == null || screenData !is T) {
            publishErrorState(errorMessage)
        } else {
            action.invoke(screenData)
        }
    }

}