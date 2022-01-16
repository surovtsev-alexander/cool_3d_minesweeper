package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessor
import logcat.logcat

class TestEventProcessor: EventProcessor<TestEvent> {
    override fun processEvent(event: Event) {
        logcat {
            "processEvent: $event"
        }
    }
}
