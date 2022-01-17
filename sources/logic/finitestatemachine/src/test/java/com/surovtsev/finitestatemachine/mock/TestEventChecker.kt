package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State

class TestEventChecker: EventChecker<TestEvent, TestFSMData> {
    override fun check(
        event: Event,
        state: State<TestFSMData>
    ): EventCheckerResult {
        return EventCheckerResult.Process
    }
}