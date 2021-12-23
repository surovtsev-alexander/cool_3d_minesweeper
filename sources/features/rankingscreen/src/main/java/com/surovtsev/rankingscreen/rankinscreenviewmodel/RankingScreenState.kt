package com.surovtsev.rankingscreen.rankinscreenviewmodel

import com.surovtsev.core.viewmodel.ScreenState

typealias RankingScreenState = ScreenState<out RankingScreenData>

val RankingScreenInitialState = ScreenState.Idle(
    RankingScreenData.NoData
)
