package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.state.State
import kotlinx.coroutines.CoroutineScope

class TestFSM(
    coroutineScope: CoroutineScope,
    logConfig: LogConfig,
): FiniteStateMachine<TestEvent, TestFSMData>(
    coroutineScope,
    State.Idle(TestFSMData),
    logConfig,
)
