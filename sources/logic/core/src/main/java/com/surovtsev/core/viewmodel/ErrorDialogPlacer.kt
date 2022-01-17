package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.state.State
import kotlinx.coroutines.flow.StateFlow

typealias ScreenStateFlow<T> = StateFlow<State<out T>>

interface ErrorDialogPlacer<C: EventToViewModel, D: ScreenData>: EventReceiver<C> {
    val mandatoryEvents: EventToViewModel.MandatoryEvents<C>
    val noScreenData: D
    val screenStateFlow: ScreenStateFlow<D>
}

