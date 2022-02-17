package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.stateholder.StateHolderImp
import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import kotlinx.coroutines.CoroutineScope

class TestFSM(
    coroutineScope: CoroutineScope,
    logConfig: LogConfig,
    stateHolder: StateHolder<TestFSMData> = StateHolderImp(
        StateDescriptionWithData(StateDescription.Idle, TestFSMData),
        false,
    ),
): FiniteStateMachine<TestEvent, TestFSMData>(
    stateHolder,
    TestEventHandler(stateHolder),
    coroutineScope,
    logConfig,
)
