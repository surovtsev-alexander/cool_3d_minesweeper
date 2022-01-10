package com.surovtsev.core.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface ErrorDialogPlacer<C: EventToViewModel, D: ScreenData>: EventHandler<C> {
    val mandatoryEvents: EventToViewModel.MandatoryEvents<C>
    val noScreenData: D
    val state: StateFlow<ScreenState<out D>>
}
