package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event

interface EventHandler<E: Event> {
    fun handleEvent(
        event: E
    ): EventHandlingResult<E>
}
