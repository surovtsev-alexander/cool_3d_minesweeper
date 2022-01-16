package com.surovtsev.finitestatemachine.state

import com.surovtsev.finitestatemachine.state.data.Data

data class StateWithData<D: Data>(
    val state: State,
    val data: D
)
