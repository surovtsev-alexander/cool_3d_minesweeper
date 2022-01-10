@file:JvmName("FSMCoreHelperKt")

package com.surovtsev.finitestatemachine.helpers.concrete

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

typealias FSMState<D> = State<out D>

interface FSMStateHelper<D: Data> {
    val state: StateFlow<FSMState<D>>

    suspend fun publishLoadingState(
        data: D = getCurrentData()
    )

    suspend fun publishErrorState(
        message: String,
        data: D = getCurrentData()
    )

    suspend fun publishIdleState(
        data: D = getCurrentData()
    )

    fun getCurrentData(): D
}

class FSMStateHelperImp<D: Data>(
    initialState: FSMState<D>,
    private val publishStateInUIThread: Boolean = false
): FSMStateHelper<D> {
    private val _state: MutableStateFlow<FSMState<D>> = MutableStateFlow(initialState)
    override val state: StateFlow<FSMState<D>> = _state.asStateFlow()

    override suspend fun publishLoadingState(
        data: D
    ) {
        publishNewState(
            State.Loading(
                data
            )
        )
    }

    override suspend fun publishErrorState(
        message: String,
        data: D
    ) {
        publishNewState(
            State.Error(
                data,
                message
            )
        )
    }

    override suspend fun publishIdleState(
        data: D
    ) {
        publishNewState(
            State.Idle(
                data
            )
        )
    }

    private suspend fun publishNewState(
        newState: State<out D>
    ) {
        if (publishStateInUIThread) {
            withContext(Dispatchers.Main) {
                _state.value = newState
            }
        }
    }

    override fun getCurrentData(): D {
        return state.value.data
    }
}