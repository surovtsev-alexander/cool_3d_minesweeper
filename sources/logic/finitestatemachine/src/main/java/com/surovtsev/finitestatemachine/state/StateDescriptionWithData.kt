package com.surovtsev.finitestatemachine.state

import com.surovtsev.finitestatemachine.state.data.Data

data class StateDescriptionWithData<D: Data>(
    val description: StateDescription,
    val data: D
)
