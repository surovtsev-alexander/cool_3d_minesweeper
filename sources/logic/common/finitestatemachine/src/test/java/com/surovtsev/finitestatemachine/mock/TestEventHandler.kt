package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import logcat.logcat

class TestEventHandler(
    private val stateHolder: StateHolder
): EventHandler {
    override fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult {
        logcat {
            "handling: $event"
        }
        return EventHandlingResult.Process(
            EventProcessor(::ok)
        )
    }

    private suspend fun ok(
    ): EventProcessingResult {
        stateHolder.publishIdleState()

        return EventProcessingResult.Ok()
    }
}