package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.finitestatemachine.helpers.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat

open class FiniteStateMachine<E: Event, D: Data>(
    val stateHolder: StateHolder<D>,
    private val eventChecker: EventChecker<E, D>,
    private val eventProcessor: EventProcessor<E>,
    private val coroutineScope: CoroutineScope,
    private val logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1),

    private val processingWaiter: ProcessingWaiter = ProcessingWaiterImp(),
    private val fsmProcessingTrigger: FsmProcessingTrigger = FsmProcessingTriggerImp(),
    private val pausedStateHolder: PausedStateHolder = PausedStateHolder(),
) {
    companion object {
        val uiDispatcher = Dispatchers.Main
        val ioDispatcher = Dispatchers.IO
    }

    private val processingMutex = Mutex(locked = false)

    private val fsmQueueHolder = FSMQueueHolder<E>(
        pausedStateHolder,
        processingWaiter,
        fsmProcessingTrigger,
        logConfig,
    )

    init {
        coroutineScope.launch(ioDispatcher) {
            handlingEventsLoop()
        }
    }

    private suspend fun handlingEventsLoop() {
        val funcName = "handlingEventsLoop"

        do {
            val event = fsmQueueHolder.pollEvent()

            if (event == null) {
                if (logConfig.logLevel.isGreaterThan3()) {
                    logcat { "$funcName; wait for trigger processing" }
                }
                fsmProcessingTrigger.waitForTriggerProcessing()
                processingWaiter.processingHasStarted()
                continue
            }

            processEvent(event)
        } while (true)
    }

    fun receiveEvent(
        event: E
    ) {
        if (logConfig.logLevel.isGreaterThan0()) {
            logcat { "handleEvent: $event" }
        }

        if (event.doNotPushToQueue) {
            if (fsmProcessingTrigger.isBusy()) {
                if (logConfig.logLevel.isGreaterThan0()) {
                    logcat { "doNotPushToQueue and skipIfBusy are true in event; FSM is Busy; skipping: $event" }
                }
                return
            }
            coroutineScope.launch(ioDispatcher) {
                processEvent(event)
            }
        } else {
            coroutineScope.launch(ioDispatcher) {
                fsmQueueHolder.pushEvent(event)
            }
        }
    }

    private suspend fun processEvent(
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
