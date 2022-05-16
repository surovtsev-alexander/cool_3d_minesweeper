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
    val initialState: State = defaultInitialState
) {

    companion object {
        val defaultInitialState = State(
            Description.Idle,
            Data.NoData,
        )

        fun createState(
            newDescription: Description,
            newData: Data,
        ) = State(
            newDescription,
            newData,
        )
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        initialState
    )

    val state: StateFlow<State> = _state.asStateFlow()

    val data: Data
        get() = state.value.data

    suspend fun pushInitialState() {
        publishNewState(initialState)
    }

    fun toLoadingState(
        newData: Data = data
    ) = createState(
        Description.Loading,
        newData,
    )

    fun toErrorState(
        message: String,
        newData: Data = data
    ) = createState(
        Description.Error(
            message
        ),
        newData,
    )

    fun toIdleState(
        newData: Data = data,
    ) = createState(
        Description.Idle,
        newData
    )

    suspend fun publishNewState(
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
