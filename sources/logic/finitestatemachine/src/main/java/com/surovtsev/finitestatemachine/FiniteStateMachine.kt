package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.helpers.FSMCoreHelper
import com.surovtsev.finitestatemachine.helpers.concrete.State
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.CoroutineScope

open class FiniteStateMachine<E: Event, D: Data>(
    eventChecker: EventChecker<E, D>,
    eventProcessor: EventProcessor<E>,
    coroutineScope: CoroutineScope,
    publishStateInUIThread: Boolean = false,
    initialState: State<D>,
    logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1)
): FSMCoreHelper<E, D>(
    eventChecker,
    eventProcessor,
    coroutineScope,
    publishStateInUIThread,
    initialState,
    logConfig
)