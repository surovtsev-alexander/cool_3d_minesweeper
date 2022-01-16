package com.surovtsev.finitestatemachine.state

sealed interface State {
    class Error(
        val message: String,
    ): State

    object Idle: State

    object Loading: State
}
