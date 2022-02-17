package com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data

interface EventChecker<E: Event, D: Data> {
    fun check(
        event: E,
        state: State<D>,
    ): EventCheckerResult<E>
}
