package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import logcat.logcat

class TestEventProcessor(
    private val stateHolder: StateHolder<TestFSMData>
): EventProcessor<TestEvent>{
    override suspend fun processEvent(event: TestEvent): EventProcessingResult<TestEvent> {
        logcat {
            "processEvent: $event"
        }
        stateHolder.publishIdleState()
        return EventProcessingResult.Processed()
    }
}
