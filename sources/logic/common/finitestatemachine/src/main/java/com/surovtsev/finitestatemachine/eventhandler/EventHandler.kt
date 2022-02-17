package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data


interface EventHandler<E: Event, D: Data> {
    fun handleEvent(
        event: E,
        state: State<D>
    ): EventHandlingResult<E>
}
