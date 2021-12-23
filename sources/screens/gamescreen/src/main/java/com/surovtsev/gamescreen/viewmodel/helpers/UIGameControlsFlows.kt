package com.surovtsev.gamescreen.viewmodel.helpers

import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.utils.timers.TimeSpanFlow
import kotlinx.coroutines.flow.StateFlow

class UIGameControlsFlows(
    val flagging: StateFlow<Boolean>,
    val showDialog: StateFlow<Boolean>,
    val bombsLeft: BombsLeftFlow,
    val timeSpan: TimeSpanFlow,
)
