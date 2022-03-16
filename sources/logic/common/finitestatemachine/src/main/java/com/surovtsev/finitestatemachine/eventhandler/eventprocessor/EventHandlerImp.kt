package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.helpers.FsmProcessingTrigger
import com.surovtsev.finitestatemachine.helpers.PausedStateHolder
import com.surovtsev.finitestatemachine.state.State

class EventHandlerImp(
    private val pausedStateHolder: PausedStateHolder,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
): EventHandler {
    override fun handleEvent(
        event: Event,
        state: State,
    ): EventHandlingResult {
        val eventProcessor = when (event) {
            is Event.Pause  -> ::pause
            is Event.Resume -> ::resume
            else            -> null
        }

        return EventHandlingResult.GeneratorHelper.processOrSkipIfNull(eventProcessor)
    }

    private suspend fun pause(
    ): EventProcessingResult {
        pausedStateHolder.pause()

        return EventProcessingResult.Ok()
    }

    private suspend fun resume(
    ): EventProcessingResult {
        pausedStateHolder.resume()

        fsmProcessingTrigger.kickFSM()

        return EventProcessingResult.Ok()
    }
}