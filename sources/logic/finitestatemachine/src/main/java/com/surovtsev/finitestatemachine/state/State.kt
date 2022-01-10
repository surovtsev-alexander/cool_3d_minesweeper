package com.surovtsev.finitestatemachine.state

import com.surovtsev.finitestatemachine.state.data.Data

sealed class State<D: Data>(
    val data: D
) {
    class ERROR<D: Data>(
        data: D
    ): State<D>(data)

    class IDLE<D: Data>(
        data: D
    ): State<D>(data)

    class LOADING<D: Data>(
        data: D
    ): State<D>(data)
}
