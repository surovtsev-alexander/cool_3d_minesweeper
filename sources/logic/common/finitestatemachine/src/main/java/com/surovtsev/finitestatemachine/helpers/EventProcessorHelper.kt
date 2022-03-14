package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandlers
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat

class EventProcessorHelper(
    private val logConfig: LogConfig,
    private val stateHolder: StateHolder,
    private val pausedStateHolder: PausedStateHolder,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
    private val eventHandlers: EventHandlers,
    private val fsmQueueHolder: FSMQueueHolder,
) {
    private val processingMutex = Mutex(locked = false)

    suspend fun processEvent(
        event: Event
    ) {
        processingMutex.withLock {
            if (logConfig.logLevel.isGreaterThan2()) {
                logcat { "processEvent; event: $event" }
            }

            when (event) {
                is Event.Pause -> {
                    pausedStateHolder.pause()
                }
                is Event.Resume -> {
                    pausedStateHolder.resume()

                    fsmProcessingTrigger.kickFSM()
                }
            }

            do {
                logcat { "handleEvent: $event" }

                val currState = stateHolder.state.value
                val handlingResult = eventHandlers.map {
                    it.handleEvent(event, currState)
                }

                handlingResult.firstOrNull {
                    it !is EventHandlingResult.Skip
                } ?: break

                val firstError = handlingResult.firstOrNull {
                    it is EventHandlingResult.RaiseError
                }

                if (firstError is EventHandlingResult.RaiseError) {
                    stateHolder.publishErrorState(
                        firstError.message
                    )
                    break
                }

                val changeEventResults =
                    handlingResult.filterIsInstance<EventHandlingResult.ChangeWith>()

                val eventToProcess = when (changeEventResults.count()) {
                    0 -> {
                        event
                    }
                    1 -> {
                        // TODO: 22.02.2022 implement
                        stateHolder.publishErrorState(
                            "internal error 1"
                        )
                        break
                    }
                    else -> {
                        stateHolder.publishErrorState(
                            "internal error 2"
                        )
                        break
                    }
                }

                if (eventToProcess.setLoadingStateBeforeProcessing) {
                    stateHolder.publishLoadingState()
                }

                val processingResults = handlingResult.map {
                    if (it is EventHandlingResult.Process) {
                        it.eventProcessor.invoke()
                    } else {
                        null
                    }
                }

                logcat { "processingResults: $processingResults" }

                processingResults
                    .filterIsInstance<EventProcessingResult.Ok>()
                    .map {
                        it.newEventToPush?.let { e ->
                            logcat { "pushEvent: $e" }
                            fsmQueueHolder.pushEvent(e)
                        }
                    }
            } while (false)
        }
    }
}