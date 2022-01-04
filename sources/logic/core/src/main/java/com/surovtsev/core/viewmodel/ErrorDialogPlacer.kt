package com.surovtsev.core.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface ErrorDialogPlacer<C: CommandFromScreen, D: ScreenData>: ScreenCommandHandler<C> {
    val baseCommands: CommandFromScreen.BaseCommands<C>
    val noScreenData: D
    val state: StateFlow<ScreenState<out D>>
}
