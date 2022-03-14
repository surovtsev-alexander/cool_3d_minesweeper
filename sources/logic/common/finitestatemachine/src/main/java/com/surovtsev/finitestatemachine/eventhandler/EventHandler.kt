package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data


interface EventHandler<D: Data> {
    fun handleEvent(
        event: Event,
        state: State<D>
    ): EventHandlingResult
}
