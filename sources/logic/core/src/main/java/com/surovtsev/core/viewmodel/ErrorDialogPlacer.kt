package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.helpers.concrete.FSMState
import kotlinx.coroutines.flow.StateFlow

typealias ScreenStateFlow<T> = StateFlow<FSMState<out T>>

interface ErrorDialogPlacer<C: EventToViewModel, D: ScreenData>: EventHandler<C> {
    val mandatoryEvents: EventToViewModel.MandatoryEvents<C>
    val noScreenData: D
    val screenStateFlow: ScreenStateFlow<D>
}

