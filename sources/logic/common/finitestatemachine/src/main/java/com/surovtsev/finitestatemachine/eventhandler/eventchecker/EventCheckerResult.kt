package com.surovtsev.finitestatemachine.eventhandler.eventchecker

import com.surovtsev.finitestatemachine.event.Event

sealed interface EventCheckerResult<E: Event> {
    class Unchecked<E: Event>: EventCheckerResult<E>

    class Pass<E: Event>: EventCheckerResult<E>

    class Skip<E: Event>: EventCheckerResult<E>

    class ChangeWith<E: Event>(
        val event: E,
    ): EventCheckerResult<E>

    class RaiseError<E: Event>(
        val message: String
    ): EventCheckerResult<E>
}
