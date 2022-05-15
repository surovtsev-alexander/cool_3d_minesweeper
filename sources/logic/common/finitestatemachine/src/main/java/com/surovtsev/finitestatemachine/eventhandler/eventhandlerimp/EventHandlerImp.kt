package com.surovtsev.finitestatemachine.eventhandler.eventhandlerimp

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toLastPriorityEventProcessor
import com.surovtsev.finitestatemachine.helpers.FSMQueueHolder
import com.surovtsev.finitestatemachine.helpers.FsmProcessingTrigger
import com.surovtsev.finitestatemachine.helpers.PausedStateHolder
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.utils.coroutines.customcoroutinescope.BeforeStartAction
import kotlinx.coroutines.delay

typealias RestartFSMAction = (beforeStartAction: BeforeStartAction) -> Unit

class EventHandlerImp(
    private val stateHolder: StateHolder,
    private val pausedStateHolder: PausedStateHolder,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
    private val fsmQueueHolder: FSMQueueHolder,
    private val restartFSMAction: RestartFSMAction,
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

        return EventHandlingResult.GeneratorHelper.processOrSkipIfNull(
            eventProcessor.toLastPriorityEventProcessor()
        )
    }

    private suspend fun toDefault(
    ): EventProcessingResult {
        // restart coroutines scope
        restartFSMAction.invoke {
            pauseAction()

            fsmQueueHolder.emptyQueue()

            stateHolder.publishDefaultInitialState()

            resumeAction()
        }

        // add suspending point to
        delay(1)

        // coroutine scope is restarted,
        // so continuation after suspending point is should not be scheduled
        // and this code is should never be executed
        assert(false)

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
