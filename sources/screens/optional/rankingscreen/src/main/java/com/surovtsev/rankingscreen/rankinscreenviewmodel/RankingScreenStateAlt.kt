package com.surovtsev.rankingscreen.rankinscreenviewmodel

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData

val RankingScreenInitialStateAlt = StateDescriptionWithData(
    StateDescription.Idle, RankingScreenDataAlt.NoData
)
