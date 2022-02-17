package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State

class TestEventChecker: EventChecker<TestEvent, TestFSMData> {
    override fun check(
        event: TestEvent,
        state: State<TestFSMData>
    ): EventCheckerResult<TestEvent> {
        return EventCheckerResult.Pass()
    }
}
