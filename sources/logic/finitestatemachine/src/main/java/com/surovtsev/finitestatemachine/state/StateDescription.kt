package com.surovtsev.finitestatemachine.state

sealed interface StateDescription {
    class Error(
        val message: String,
    ): StateDescription

    object Idle: StateDescription

    object Loading: StateDescription
}
