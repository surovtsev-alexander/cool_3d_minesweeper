package com.surovtsev.finitestatemachine.state

import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.state.description.Description

data class State(
    val description: Description,
    val data: Data
)
