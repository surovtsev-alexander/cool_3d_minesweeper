@file:JvmName("FSMCoreHelperKt")

package com.surovtsev.finitestatemachine.helpers.concrete

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateWithData
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

typealias FSMState<D> = StateWithData<out D>

interface FSMStateHolder<D: Data> {
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

    suspend fun publishNewState(
        newState: State,
        data: D = getCurrentData(),
    )

    fun getCurrentData(): D
}

class FSMStateHolderImp<D: Data>(
    initialState: FSMState<D>,
    private val publishStateInUIThread: Boolean = false
): FSMStateHolder<D> {
    private val _state: MutableStateFlow<FSMState<D>> = MutableStateFlow(initialState)
    override val state: StateFlow<FSMState<D>> = _state.asStateFlow()

    override suspend fun publishLoadingState(
        data: D
    ) {
        publishNewState(
            State.Loading,
            data
        )
    }

    override suspend fun publishErrorState(
        message: String,
        data: D
    ) {
        publishNewState(
            State.Error(
                message
            ),
            data
        )
    }

    override suspend fun publishIdleState(
        data: D
    ) {
        publishNewState(
            State.Idle,
            data,
        )
    }

    override suspend fun publishNewState(
        newState: State,
        data: D,
    ) {
        if (publishStateInUIThread) {
            withContext(Dispatchers.Main) {
                _state.value = StateWithData(
                    newState,
                    data
                )
            }
        }
    }

    override fun getCurrentData(): D {
        return state.value.data
    }
}