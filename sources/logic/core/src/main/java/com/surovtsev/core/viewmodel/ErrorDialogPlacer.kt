package com.surovtsev.core.viewmodel

import androidx.lifecycle.LiveData

interface ErrorDialogPlacer<C: CommandFromScreen, D: ScreenData>: ScreenCommandHandler<C> {
    val baseCommands: CommandFromScreen.BaseCommands<C>
    val noScreenData: D
    val state: LiveData<ScreenState<out D>>
}
