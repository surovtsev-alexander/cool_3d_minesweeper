package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.state.State
import kotlinx.coroutines.flow.StateFlow

typealias ScreenStateFlow<T> = StateFlow<State<out T>>

interface ErrorDialogPlacer<D: ScreenData> {
    val mandatoryEvents: EventToViewModel.MandatoryEvents
    val noScreenData: D
    val screenStateFlow: ScreenStateFlow<D>
    val finiteStateMachine: FiniteStateMachine<D>
}

