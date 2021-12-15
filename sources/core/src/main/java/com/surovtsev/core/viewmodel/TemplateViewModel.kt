package com.surovtsev.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl

abstract class TemplateViewModel<C: CommandsFromScreen, D: ScreenData>(
    private val noScreenData: D
):
    ViewModel(),
    ScreenCommandsHandler<C>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{
    protected abstract val dataHolder: MutableLiveData<ScreenState<out D>>
    abstract val dataValue: LiveData<ScreenState<out D>>


    protected suspend fun setLoadingState() {
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
        screenState: ScreenState<D>
    ) {
        withUIContext {
            dataHolder.value = screenState
        }
    }


}