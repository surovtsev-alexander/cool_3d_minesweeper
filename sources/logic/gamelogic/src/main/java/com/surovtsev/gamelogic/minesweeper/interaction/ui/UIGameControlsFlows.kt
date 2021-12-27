package com.surovtsev.gamelogic.minesweeper.interaction.ui

import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.BombsLeftFlow
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
