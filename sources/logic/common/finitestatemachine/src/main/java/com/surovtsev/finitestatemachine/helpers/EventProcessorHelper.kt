package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
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
    private val eventHandler: EventHandler<E, D>,
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

            val eventHandlingResult = eventHandler.handleEvent(
                event,
                stateHolder.state.value
            )

            val errorMessage = when (eventHandlingResult) {
                is EventHandlingResult.Process -> {
                    if (event.setLoadingStateBeforeProcessing) {
                        stateHolder.publishLoadingState()
                    }
                    val eventProcessingResult = eventHandlingResult.eventProcessor.invoke()
                    if (eventProcessingResult !is EventProcessingResult.Ok<*>) {
                        "internal error 1"
                    } else {
                        null
                    }
                }
                is EventHandlingResult.RaiseError -> {
                    eventHandlingResult.message
                }
                is EventHandlingResult.Skip -> {
                    "internal error 2"
                }
                is EventHandlingResult.ChangeWith -> {
                    // todo: add implementation
                    assert(false)
                    null
                }
            }

            errorMessage?.let {
                stateHolder.publishErrorState(
                    it
                )
            }
        }
    }
}