package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.finitestatemachine.helpers.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logcat.logcat

open class FiniteStateMachine<E: Event, D: Data>(
    val stateHolder: StateHolder<D>,
    private val eventChecker: EventChecker<E, D>,
    private val eventProcessor: EventProcessor<E>,
    private val coroutineScope: CoroutineScope,
    private val logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1),
) {
    companion object {
        val uiDispatcher = Dispatchers.Main
        val ioDispatcher = Dispatchers.IO
    }

    private val processingWaiter: ProcessingWaiter = ProcessingWaiterImp()
    private val fsmProcessingTrigger: FsmProcessingTrigger = FsmProcessingTriggerImp()
    private val pausedStateHolder: PausedStateHolder = PausedStateHolder()

    val queueHolder = FSMQueueHolder<E>(
        pausedStateHolder,
        processingWaiter,
        fsmProcessingTrigger,
        logConfig,
    )

    private val eventProcessorHelper = EventProcessorHelper(
        logConfig,
        stateHolder,
        pausedStateHolder,
        fsmProcessingTrigger,
        eventChecker,
        eventProcessor,
    )

    init {
        coroutineScope.launch(ioDispatcher) {
            handlingEventsLoop()
        }
    }

    private suspend fun handlingEventsLoop() {
        val funcName = "handlingEventsLoop"

        do {
            val event = queueHolder.pollEvent()

            if (event == null) {
                if (logConfig.logLevel.isGreaterThan3()) {
                    logcat { "$funcName; wait for trigger processing" }
                }
                fsmProcessingTrigger.waitForTriggerProcessing()
                processingWaiter.processingHasStarted()
                continue
            }

            eventProcessorHelper.processEvent(event)
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
                eventProcessorHelper.processEvent(event)
            }
        } else {
            coroutineScope.launch(ioDispatcher) {
                queueHolder.pushEvent(event)
            }
        }
    }
}
