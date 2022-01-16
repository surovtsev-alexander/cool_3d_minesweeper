package com.surovtsev.rankingscreen.rankinscreenviewmodel

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateWithData

val RankingScreenInitialState = StateWithData(
    State.Idle,
    RankingScreenData.NoData
)
