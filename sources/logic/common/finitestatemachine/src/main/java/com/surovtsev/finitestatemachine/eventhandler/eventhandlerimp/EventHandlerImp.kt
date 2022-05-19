package com.surovtsev.finitestatemachine.eventhandler.eventhandlerimp

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toLastPriorityEventProcessor
import com.surovtsev.finitestatemachine.helpers.InternalLowLevelCommandsHandler
import com.surovtsev.finitestatemachine.state.State
import kotlinx.coroutines.delay

class EventHandlerImp(
    private val internalLowLevelCommandsHandler: InternalLowLevelCommandsHandler,
): EventHandler {

    override val transitions: List<EventHandler.Transition> = emptyList()

    override fun handleEvent(
        event: Event,
        state: State,
    ): EventHandlingResult {
        val eventProcessor = when (event) {
            is Event.TurnOff    -> ::turnOff
            is Event.Restart    -> suspend { restart(event.startingEvent) }
            is Event.Pause      -> ::pause
            is Event.Resume     -> ::resume
            else                -> null
        }

        return EventHandlingResult.GeneratorHelper.processOrSkipIfNull(
            eventProcessor.toLastPriorityEventProcessor()
        )
    }

    private suspend fun turnOff(
    ): EventProcessingResult {
        internalLowLevelCommandsHandler.stop()

        // See restart()
        delay(1)

        return EventProcessingResult.Ok()
    }

    private suspend fun restart(
        startingEvent: Event
    ): EventProcessingResult {
        // Restart coroutines scope.
        internalLowLevelCommandsHandler.restart(startingEvent)

        // Add suspending point.
        delay(1)

        // Coroutine scope is restarted,
        // so continuation after suspending point is should not be scheduled
        // and this code is should never be executed
        assert(false)

        return EventProcessingResult.Ok()
    }

    private suspend fun pause(
    ): EventProcessingResult {
        internalLowLevelCommandsHandler.pause()

        return EventProcessingResult.Ok()
    }

    private suspend fun resume(
    ): EventProcessingResult {
        internalLowLevelCommandsHandler.resume()

        return EventProcessingResult.Ok()
    }
}
