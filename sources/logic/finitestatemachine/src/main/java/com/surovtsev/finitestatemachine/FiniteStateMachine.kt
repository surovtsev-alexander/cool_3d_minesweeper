package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.helpers.FSMCoreHelper
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.CoroutineScope
import logcat.logcat

open class FiniteStateMachine<E: Event, D: Data>(
    coroutineScope: CoroutineScope,
    initialState: State<out D>,
    logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1)
): FSMCoreHelper<E, D>(
    coroutineScope,
    initialState,
    logConfig
) {

    open fun handleEvent(
        event: E
    ) {
        if (logConfig.logLevel.isGreaterThan0()) {
            logcat { "handleEvent: $event" }
        }

        if (isBusy() && event.skipIfFSMIsBusy) {

            if (logConfig.logLevel.isGreaterThan0()) {
                logcat { "FSM is Busy; skipIfFSMIsBusy is true in event; skipping: $event" }
            }

            return
        }

        pushEvent(event)
    }
}