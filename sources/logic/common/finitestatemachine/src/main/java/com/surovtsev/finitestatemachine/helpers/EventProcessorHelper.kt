package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.eventhandlerOld.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandlerOld.eventprocessor.EventProcessor
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
    private val eventChecker: EventChecker<E, D>,
    private val eventProcessor: EventProcessor<E>,
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

            val eventCheckingResult = eventChecker.check(
                event,
                stateHolder.state.value
            )

            val errorMessage = when (eventCheckingResult) {
                is EventCheckerResult.Pass -> {
                    if (event.setLoadingStateBeforeProcessing) {
                        stateHolder.publishLoadingState()
                    }
                    val eventProcessingResult = eventProcessor.processEvent(event)
                    if (eventProcessingResult !is EventProcessingResult.Processed<*>) {
                        "internal error 1"
                    } else {
                        null
                    }
                }
                is EventCheckerResult.RaiseError -> {
                    eventCheckingResult.message
                }
                is EventCheckerResult.Unchecked -> {
                    "internal error 2"
                }
                is EventCheckerResult.Skip -> {
                    // todo: add implementation
                    assert(false)
                    null
                }
                is EventCheckerResult.ChangeWith -> {
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