package com.surovtsev.rankingscreen.rankinscreenviewmodel

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateWithData

val RankingScreenInitialStateAlt = StateWithData(
    State.Idle, RankingScreenDataAlt.NoData
)
