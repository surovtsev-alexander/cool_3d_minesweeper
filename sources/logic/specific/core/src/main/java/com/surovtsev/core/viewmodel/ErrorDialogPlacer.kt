package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.state.State
import kotlinx.coroutines.flow.StateFlow

typealias ScreenStateFlow<T> = StateFlow<State<out T>>

interface ErrorDialogPlacer<E: EventToViewModel, D: ScreenData> {
    val mandatoryEvents: EventToViewModel.MandatoryEvents<E>
    val noScreenData: D
    val screenStateFlow: ScreenStateFlow<D>
    val finiteStateMachine: FiniteStateMachine<E, D>
}

