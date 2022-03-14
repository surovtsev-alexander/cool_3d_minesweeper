package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.state.State
import kotlinx.coroutines.flow.StateFlow

typealias ScreenStateFlow = StateFlow<State>


interface ErrorDialogPlacer {
    val screenStateFlow: ScreenStateFlow
    val finiteStateMachine: FiniteStateMachine
}
