package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine

import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.finitestatemachine.state.State

val RankingScreenInitialState = State(
    Description.Idle,
    RankingScreenData.NoData
)
