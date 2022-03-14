package com.surovtsev.finitestatemachine.stateholder

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.state.description.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext


class StateHolder(
    private val publishStateInUIThread: Boolean = false,
    initialState: State = defaultInitialState
) {

    companion object {
        val defaultInitialState = State(
            Description.Idle,
            Data.NoData,
        )
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        initialState
    )

    val state: StateFlow<State> = _state.asStateFlow()

    val data: Data
        get() = state.value.data

    suspend fun publishDefaultInitialState() {
        publishNewState(defaultInitialState)
    }

    suspend fun publishLoadingState(
        newData: Data = data
    ) {
        publishNewState(
            Description.Loading,
            newData
        )
    }

    suspend fun publishErrorState(
        message: String,
        newData: Data = data
    ) {
        publishNewState(
            Description.Error(
                message
            ),
            newData
        )
    }

    suspend fun publishIdleState(
        newData: Data = data
    ) {
        publishNewState(
            Description.Idle,
            newData,
        )
    }

    private suspend fun publishNewState(
        newDescription: Description,
        newData: Data,
    ) {
        publishNewState(
            State(
                newDescription,
                newData
            )
        )
    }

    private suspend fun publishNewState(
        state: State
    ) {
        val publishingAction = {
            _state.value = state
        }

        if (publishStateInUIThread) {
            withContext(Dispatchers.Main) {
                publishingAction.invoke()
            }
        } else {
            publishingAction.invoke()
        }
    }
}
