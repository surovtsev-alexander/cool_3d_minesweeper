package com.surovtsev.ranking.rankinscreenviewmodel

import com.surovtsev.utils.viewmodel.ScreenState

typealias RankingScreenState = ScreenState<RankingScreenData>

val RankingScreenInitialState = ScreenState.Idle(
    RankingScreenData.NoData)
