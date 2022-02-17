package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import logcat.logcat

class TestEventHandler(
    private val stateHolder: StateHolder<TestFSMData>
): EventHandler<TestEvent, TestFSMData> {
    override fun handleEvent(
        event: TestEvent,
        state: State<TestFSMData>
    ): EventHandlingResult<TestEvent> {
        logcat {
            "handling: $event"
        }
        return EventHandlingResult.Process(
            ::ok
        )
    }

    private suspend fun ok(
    ): EventProcessingResult<TestEvent> {
        stateHolder.publishIdleState()

        return EventProcessingResult.Ok()
    }
}