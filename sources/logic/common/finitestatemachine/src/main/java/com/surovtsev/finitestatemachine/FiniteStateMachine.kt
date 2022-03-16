package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlers
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventHandlerImp
import com.surovtsev.finitestatemachine.helpers.*
import com.surovtsev.finitestatemachine.interfaces.EventReceiver
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logcat.logcat

class FiniteStateMachine(
    val stateHolder: StateHolder,
    userEventHandlers: EventHandlers,
    subscriptionsHolder: SubscriptionsHolder,
    private val logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1),
): EventReceiver, Subscription {
    companion object {
        val uiDispatcher = Dispatchers.Main
        val ioDispatcher = Dispatchers.IO
    }

    private val processingWaiter: ProcessingWaiter = ProcessingWaiterImp()
    private val fsmProcessingTrigger: FsmProcessingTrigger = FsmProcessingTriggerImp()
    private val pausedStateHolder: PausedStateHolder = PausedStateHolder()

    val queueHolder = FSMQueueHolder(
        pausedStateHolder,
        processingWaiter,
        fsmProcessingTrigger,
        logConfig,
    )

    private val baseEventHandler: EventHandler = EventHandlerImp(
        pausedStateHolder,
        fsmProcessingTrigger,
    )

    private val eventHandlers = listOf(baseEventHandler) + userEventHandlers

    private val eventProcessorHelper = EventProcessorHelper(
        logConfig,
        stateHolder,
        eventHandlers,
        queueHolder,
    )

    private val coroutineScope = subscriptionsHolder.coroutineScope

    init {
        subscriptionsHolder
            .addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
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

    override fun receiveEvent(
        event: Event
    ) {
        if (logConfig.logLevel.isGreaterThan0()) {
            logcat { "receiveEvent: $event" }
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
