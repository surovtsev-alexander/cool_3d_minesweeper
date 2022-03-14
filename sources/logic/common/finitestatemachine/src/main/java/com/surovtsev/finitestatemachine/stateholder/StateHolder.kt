package com.surovtsev.finitestatemachine.stateholder

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.state.description.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext


interface StateHolder {
    val state: StateFlow<State>

    suspend fun publishLoadingState(
        newData: Data = data
    )

    suspend fun publishErrorState(
        message: String,
        newData: Data = data
    )

    suspend fun publishIdleState(
        newData: Data = data
    )

    suspend fun publishNewState(
        newDescription: Description,
        newData: Data = data,
    )

    val data: Data
}

class StateHolderImp(
    initialState: State,
    private val publishStateInUIThread: Boolean = false
): StateHolder {
    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    override val state: StateFlow<State> = _state.asStateFlow()

    override suspend fun publishLoadingState(
        newData: Data
    ) {
        publishNewState(
            Description.Loading,
            newData
        )
    }

    override suspend fun publishErrorState(
        message: String,
        newData: Data
    ) {
        publishNewState(
            Description.Error(
                message
            ),
            newData
        )
    }

    override suspend fun publishIdleState(
        newData: Data
    ) {
        publishNewState(
            Description.Idle,
            newData,
        )
    }

    override suspend fun publishNewState(
        newDescription: Description,
        newData: Data,
    ) {
        val publishingAction = {
            _state.value = State(
                newDescription,
                newData,
            )
        }

        if (publishStateInUIThread) {
            withContext(Dispatchers.Main) {
                publishingAction.invoke()
            }
        } else {
            publishingAction.invoke()
        }
    }

    override val data: Data
        get() = state.value.data
}