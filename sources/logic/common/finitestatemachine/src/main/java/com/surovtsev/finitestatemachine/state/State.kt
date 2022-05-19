package com.surovtsev.finitestatemachine.state

import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.state.description.Description

data class State(
    val description: Description,
    val data: Data
)

fun State.toLoading(
    newData: Data = data
) = State(
    Description.Loading,
    newData,
)

fun State.toError(
    message: String,
    newData: Data = data
) = State(
    Description.Error(
        message
    ),
    newData,
)

fun State.toIdle(
    newData: Data = data,
) = State(
    Description.Idle,
    newData
)
