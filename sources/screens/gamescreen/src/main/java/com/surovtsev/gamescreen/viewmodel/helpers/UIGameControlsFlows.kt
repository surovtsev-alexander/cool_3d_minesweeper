package com.surovtsev.gamescreen.viewmodel.helpers

import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.utils.timers.async.TimeSpanFlow
import kotlinx.coroutines.flow.StateFlow

typealias FPSFlow = StateFlow<Float>

class UIGameControlsFlows(
    val flagging: StateFlow<Boolean>,
    val uiGameStatus: StateFlow<UIGameStatus>,
    val bombsLeft: BombsLeftFlow,
    val timeSpan: TimeSpanFlow,
    val fpsFlow: FPSFlow,
)
