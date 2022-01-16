package com.surovtsev.finitestatemachine.eventchecker

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.helpers.concrete.FSMState
import com.surovtsev.finitestatemachine.state.data.Data

interface EventChecker<E: Event, D: Data> {
    fun check(
        event: Event,
        fsmState: FSMState<D>,
    ): EventCheckerResult
}
