package com.surovtsev.gamescreen.viewmodel.helpers

import kotlinx.coroutines.flow.StateFlow

class UIGameControlsFlows(
    val flagging: StateFlow<Boolean>,
    val showDialogEvent: StateFlow<Boolean>
)
