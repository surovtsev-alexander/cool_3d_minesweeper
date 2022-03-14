package com.surovtsev.finitestatemachine.state.description

sealed interface Description {
    class Error(
        val message: String,
    ): Description

    object Idle: Description

    object Loading: Description
}
