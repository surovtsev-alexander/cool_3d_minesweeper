package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.helpers.FSMQueueHolder
import com.surovtsev.finitestatemachine.helpers.FsmProcessingTrigger
import com.surovtsev.finitestatemachine.helpers.PausedStateHolder
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.stateholder.StateHolder

class EventHandlerImp(
    private val stateHolder: StateHolder,
    private val pausedStateHolder: PausedStateHolder,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
    private val fsmQueueHolder: FSMQueueHolder,
): EventHandler {
    override fun handleEvent(
        event: Event,
        state: State,
    ): EventHandlingResult {
        val eventProcessor = when (event) {
            is Event.ToDefault  -> ::toDefault
            is Event.Pause      -> ::pause
            is Event.Resume     -> ::resume
            else                -> null
        }

        return EventHandlingResult.GeneratorHelper.processOrSkipIfNull(eventProcessor)
    }

    private suspend fun toDefault(
    ): EventProcessingResult {
        pauseAction()

        fsmQueueHolder.emptyQueue()

        stateHolder.publishDefaultInitialState()

        resumeAction()

        return EventProcessingResult.Ok()
    }

    private suspend fun pause(
    ): EventProcessingResult {
        pauseAction()

        return EventProcessingResult.Ok()
    }

    private suspend fun resume(
    ): EventProcessingResult {
        resumeAction()

        return EventProcessingResult.Ok()
    }

    // region [auxiliary functions]
    private suspend fun pauseAction() {
        pausedStateHolder.pause()
    }

    private suspend fun resumeAction() {
        pausedStateHolder.resume()

        fsmProcessingTrigger.kickFSM()
    }
    // endregion [auxiliary functions]
}
