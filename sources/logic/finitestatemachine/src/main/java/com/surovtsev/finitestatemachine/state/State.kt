package com.surovtsev.finitestatemachine.state

import com.surovtsev.finitestatemachine.state.data.Data

sealed class State<D: Data>(
    val data: D
) {
    class Error<D: Data>(
        data: D,
        val message: String,
    ): State<D>(data)

    class Idle<D: Data>(
        data: D
    ): State<D>(data)

    class Loading<D: Data>(
        data: D
    ): State<D>(data)
}
