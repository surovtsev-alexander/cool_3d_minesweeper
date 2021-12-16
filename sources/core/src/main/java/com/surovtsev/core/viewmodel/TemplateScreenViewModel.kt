package com.surovtsev.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl

typealias CommandProcessor<T> = suspend () -> Unit

typealias FinishAction = () -> Unit

abstract class TemplateScreenViewModel<C: CommandFromScreen, D: ScreenData>(
    private val initCommand: C,
    private val noScreenData: D,
):
    ViewModel(),
    ScreenCommandHandler<C>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{
    var finishAction: FinishAction? = null

    protected abstract val dataHolder: MutableLiveData<ScreenState<out D>>
    abstract val dataValue: LiveData<ScreenState<out D>>


    override fun handleCommand(command: C) {
        launchOnIOThread {
            val currState = dataHolder.value

            val screenData = dataHolder.value?.screenData

            val isErrorState =
                currState != null &&
                        currState is ScreenState.Error
            val isErrorDuringInitialization =
                isErrorState &&
                        screenData != null &&
                        screenData is ScreenData.DuringInitialization


            if (isErrorState) {
                val isCloseErrorCommand = command is CommandFromScreen.CloseError

                if (isCloseErrorCommand) {
                    if (isErrorDuringInitialization) {
                        finishAction?.let {
                            closeError()
                            it.invoke()
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
            publishError("error while processing command")
        } else {
            setLoadingState()
            commandProcessor.invoke()
        }
    }

    abstract suspend fun getCommandProcessor(command: C): CommandProcessor<C>?

    private suspend fun setLoadingState() {
        publishNewState(
            ScreenState.Loading(
                getCurrentScreenDataOrNoData()
            )
        )
    }

    protected suspend fun publishError(
        message: String
    ) {
        publishNewState(
            ScreenState.Error(
                getCurrentScreenDataOrNoData(),
                message
            )
        )
    }

    protected suspend fun closeError() {
        publishNewState(
            ScreenState.Idle(
                getCurrentScreenDataOrNoData()
            )
        )
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
        val screenData = dataHolder.value?.screenData

        if (screenData == null || screenData !is T) {
            publishError(errorMessage)
        } else {
            action.invoke(screenData)
        }
    }

    protected fun getCurrentScreenDataOrNoData(): D {
        return dataHolder.value?.screenData ?: noScreenData
    }

    protected suspend fun publishNewState(
        screenState: ScreenState<out D>
    ) {
        withUIContext {
            dataHolder.value = screenState
        }
    }


}