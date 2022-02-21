package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandlers
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat

class EventProcessorHelper<E: Event, D: Data>(
    private val logConfig: LogConfig,
    private val stateHolder: StateHolder<D>,
    private val pausedStateHolder: PausedStateHolder,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
    private val eventHandlers: EventHandlers<E, D>,
    private val fsmQueueHolder: FSMQueueHolder<E>,
) {
    private val processingMutex = Mutex(locked = false)

    suspend fun processEvent(
        event: E
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
                    handlingResult.filterIsInstance<EventHandlingResult.ChangeWith<E>>()

                val eventToProcess = when (changeEventResults.count()) {
                    0 -> {
                        event
                    }
                    1 -> {
                        changeEventResults[0].event
                    }
                    else -> {
                        stateHolder.publishErrorState(
                            "internal error 1"
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

                val pushNewEventResults = processingResults
                    .filterIsInstance<EventProcessingResult.Ok<E>>()
                    .map {
                        it.newEventToPush?.let { e ->
                            fsmQueueHolder.pushEvent(e)
                        }
                    }
            } while (false)

//            val eventHandlingResult = eventHandler.handleEvent(
//                event,
//                stateHolder.state.value
//            )
//
//            val errorMessage = when (eventHandlingResult) {
//                is EventHandlingResult.Process -> {
//                    if (event.setLoadingStateBeforeProcessing) {
//                        stateHolder.publishLoadingState()
//                    }
//                    val eventProcessingResult = eventHandlingResult.eventProcessor.invoke()
//                    if (eventProcessingResult !is EventProcessingResult.Ok<*>) {
//                        "internal error 1"
//                    } else {
//                        null
//                    }
//                }
//                is EventHandlingResult.RaiseError -> {
//                    eventHandlingResult.message
//                }
//                is EventHandlingResult.Skip -> {
//                    "internal error 2"
//                }
//                is EventHandlingResult.ChangeWith -> {
//                    // todo: add implementation
//                    assert(false)
//                    null
//                }
//            }
//
//            errorMessage?.let {
//                stateHolder.publishErrorState(
//                    it
//                )
//            }
        }
    }
}