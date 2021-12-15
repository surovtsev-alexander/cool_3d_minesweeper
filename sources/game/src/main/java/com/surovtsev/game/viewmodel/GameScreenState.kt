package com.surovtsev.game.viewmodel

import com.surovtsev.core.viewmodel.ScreenState

typealias GameScreenState = ScreenState<out GameScreenData>

val GameScreenInitialState = ScreenState.Idle(
    GameScreenData.NoData
)
