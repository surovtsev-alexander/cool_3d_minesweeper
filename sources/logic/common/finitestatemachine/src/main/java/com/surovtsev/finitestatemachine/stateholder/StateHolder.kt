package com.surovtsev.finitestatemachine.stateholder

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.state.description.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext


typealias FSMStateFlow = StateFlow<State>

class StateHolder(
    private val publishStateInUIThread: Boolean = false,
    val initialState: State = defaultInitialState
) {

    companion object {
        val defaultInitialState = State(
            Description.Idle,
            Data.NoData,
        )
    }

    private val _mutableFSMStateFlow: MutableStateFlow<State> = MutableStateFlow(
        initialState
    )

    val fsmStateFlow: FSMStateFlow = _mutableFSMStateFlow.asStateFlow()

    val data: Data
        get() = fsmStateFlow.value.data

    suspend fun pushInitialState() {
        publishNewState(initialState)
    }

    suspend fun publishNewState(
        state: State
    ) {
        val publishingAction = {
            _mutableFSMStateFlow.value = state
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
