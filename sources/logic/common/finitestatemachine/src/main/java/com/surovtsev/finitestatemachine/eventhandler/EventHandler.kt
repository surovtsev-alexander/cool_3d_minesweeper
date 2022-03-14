package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.state.State


interface EventHandler {
    fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult
}
