package com.surovtsev.finitestatemachine.eventchecker

sealed interface EventCheckerResult {
    object Unchecked: EventCheckerResult

    object Process: EventCheckerResult

    class RaiseError(
        val message: String
    ): EventCheckerResult

}
