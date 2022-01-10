package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.helpers.FSMQueueHelper
import kotlinx.coroutines.CoroutineScope
import logcat.logcat

open class FiniteStateMachine<E: Event>(
    coroutineScope: CoroutineScope,
    logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1)
): FSMQueueHelper<E>(
    coroutineScope,
    logConfig
) {

    open fun handleEvent(
        event: E
    ) {
        if (logConfig.logLevel.isGreaterThan0()) {
            logcat { "handleEvent: $event" }
        }

        if (isFSMBusy() && event.skipIfFSMIsBusy) {

            if (logConfig.logLevel.isGreaterThan0()) {
                logcat { "FSM is Busy; skipIfFSMIsBusy is true in event; skipping: $event" }
            }

            return
        }

        pushEvent(event)
    }
}