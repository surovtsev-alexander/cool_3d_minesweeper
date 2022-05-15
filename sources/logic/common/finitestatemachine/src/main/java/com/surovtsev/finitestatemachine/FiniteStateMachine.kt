package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandlers
import com.surovtsev.finitestatemachine.eventhandler.eventhandlerimp.EventHandlerImp
import com.surovtsev.finitestatemachine.eventreceiver.EventReceiverImp
import com.surovtsev.finitestatemachine.helpers.*
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import kotlinx.coroutines.launch
import logcat.logcat

class FiniteStateMachine(
    val stateHolder: StateHolder,
    userEventHandlers: EventHandlers,
    private val logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1),
): Subscription {

    private val restartableCoroutineScope = DaggerRestartableCoroutineScopeComponent
        .create()

    private val customCoroutineScope: CustomCoroutineScope
        get() {
            return restartableCoroutineScope.customCoroutineScope
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

    private val baseEventHandler = EventHandlerImp(
        ::restartFSM,
        ::stop,
        ::pause,
        ::resume,
    )

    private val eventHandlers = listOf(baseEventHandler) + userEventHandlers

    private val eventProcessorHelper = EventProcessorHelper(
        logConfig,
        stateHolder,
        eventHandlers,
        queueHolder,
    )

    val eventReceiver = EventReceiverImp(
        logConfig,
        fsmProcessingTrigger,
        eventProcessorHelper,
        queueHolder,
        customCoroutineScope,
    )

    init {
        SubscriptionsHolderComponentFactoryHolderImp
            .createAndSubscribe(
                restartableCoroutineScope,
                "FiniteStateMachine"
            )
            .subscriptionsHolder
            .addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            handlingEventsLoop()
        }
    }

    fun restartFSM(
        startingEvent: Event,
    ) {
        if (logConfig.logLevel.isGreaterThan1()) {
            logcat { "restarting fsm" }
        }
        restartableCoroutineScope
            .subscriberImp
            .restart {
                pause()

                queueHolder.emptyQueue()

                stateHolder.publishDefaultInitialState()

                queueHolder.pushEvent(
                    startingEvent
                )

                resume()
            }
    }

    private fun stop() {
        logcat { "stopping fsm" }
        restartableCoroutineScope
            .subscriberImp
            .stop()
    }

    private fun pause() {
        pausedStateHolder.pause()
    }

    private fun resume() {
        pausedStateHolder.resume()

        fsmProcessingTrigger.kickFSM()
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

            eventProcessorHelper.processEvent(event, eventReceiver)
        } while (true)
    }
}
