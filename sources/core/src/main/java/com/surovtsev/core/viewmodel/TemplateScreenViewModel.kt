package com.surovtsev.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import logcat.logcat

typealias CommandProcessor = suspend () -> Unit

typealias FinishAction = () -> Unit

typealias ScreenStateValue<T> = LiveData<ScreenState<out T>>

abstract class TemplateScreenViewModel<C: CommandFromScreen, D: ScreenData>(
    private val initCommand: C,
    private val noScreenData: D,
):
    ViewModel(),
    ScreenCommandHandler<C>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{
    var finishAction: FinishAction? = null

    protected abstract val stateHolder: MutableLiveData<ScreenState<out D>>
    abstract val stateValue: LiveData<ScreenState<out D>>

    abstract suspend fun getCommandProcessor(command: C): CommandProcessor?


    override fun handleCommand(command: C) {
        launchOnIOThread {
            logcat { "handleCommand: $command" }

            val currState = stateHolder.value

            val screenData = stateHolder.value?.screenData

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
                    if (isErrorDuringInitialization) {
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
            stateHolder.value = screenState
        }
    }

    protected suspend fun closeError() {
        publishIdleState(
            getCurrentScreenDataOrNoData()
        )
    }

    protected fun getCurrentScreenDataOrNoData(): D {
        return stateHolder.value?.screenData ?: noScreenData
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
        val screenData = stateHolder.value?.screenData

        if (screenData == null || screenData !is T) {
            publishErrorState(errorMessage)
        } else {
            action.invoke(screenData)
        }
    }

}