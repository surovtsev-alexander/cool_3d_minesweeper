package com.surovtsev.finitestatemachine.eventreceiver

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.helpers.EventProcessorHelper
import com.surovtsev.finitestatemachine.helpers.FSMQueueHolder
import com.surovtsev.finitestatemachine.helpers.FsmProcessingTrigger
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat

typealias customCoroutineScopeCalculator = () -> CustomCoroutineScope

class EventReceiverImp(
    private val logConfig: LogConfig,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
    private val eventProcessorHelper: EventProcessorHelper,
    private val queueHolder: FSMQueueHolder,
    private val customCoroutineScope: CustomCoroutineScope,
): EventReceiver {

    override fun receiveEvent(
        event: Event
    ) {
        if (logConfig.logLevel.isGreaterThan0()) {
            logcat { "receiveEvent: $event" }
        }

        val eventMode = event.eventMode

        if (eventMode.doNotPushToQueue) {
            if ((eventMode !is Event.EventMode.DoNotWaitEndOfProcessing) && fsmProcessingTrigger.isBusy()) {
                if (logConfig.logLevel.isGreaterThan0()) {
                    logcat { "doNotPushToQueue is true in event; FSM is Busy; skipping: $event" }
                }
                return
            }
            customCoroutineScope.launch {
                eventProcessorHelper.processEvent(event, this@EventReceiverImp)
            }
        } else {
            customCoroutineScope.launch {
                queueHolder.pushEvent(event)
            }
        }
    }
}