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

typealias RestartFSMAction = (startingEvent: Event) -> Unit
typealias StopFSM = () -> Unit
typealias PauseAction = () -> Unit
typealias ResumeAction = () -> Unit

class EventHandlerImp(
    private val restartFSMAction: RestartFSMAction,
    private val stopFSM: StopFSM,
    private val pauseAction: PauseAction,
    private val resumeAction: ResumeAction,
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
        stopFSM()

        // see toDefault()
        delay(1)

        return EventProcessingResult.Ok()
    }

    private suspend fun restart(
        startingEvent: Event
    ): EventProcessingResult {
        // restart coroutines scope
        restartFSMAction(startingEvent)

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
}
