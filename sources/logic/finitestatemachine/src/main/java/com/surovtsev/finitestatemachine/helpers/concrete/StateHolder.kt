@file:JvmName("FSMCoreHelperKt")

package com.surovtsev.finitestatemachine.helpers.concrete

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

typealias State<D> = StateDescriptionWithData<out D>

interface StateHolder<D: Data> {
    val state: StateFlow<State<D>>

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
        newState: StateDescription,
        data: D = getCurrentData(),
    )

    fun getCurrentData(): D
}

class StateHolderImp<D: Data>(
    initialState: State<D>,
    private val publishStateInUIThread: Boolean = false
): StateHolder<D> {
    private val _state: MutableStateFlow<State<D>> = MutableStateFlow(initialState)
    override val state: StateFlow<State<D>> = _state.asStateFlow()

    override suspend fun publishLoadingState(
        data: D
    ) {
        publishNewState(
            StateDescription.Loading,
            data
        )
    }

    override suspend fun publishErrorState(
        message: String,
        data: D
    ) {
        publishNewState(
            StateDescription.Error(
                message
            ),
            data
        )
    }

    override suspend fun publishIdleState(
        data: D
    ) {
        publishNewState(
            StateDescription.Idle,
            data,
        )
    }

    override suspend fun publishNewState(
        newState: StateDescription,
        data: D,
    ) {
        if (publishStateInUIThread) {
            withContext(Dispatchers.Main) {
                _state.value = StateDescriptionWithData(
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