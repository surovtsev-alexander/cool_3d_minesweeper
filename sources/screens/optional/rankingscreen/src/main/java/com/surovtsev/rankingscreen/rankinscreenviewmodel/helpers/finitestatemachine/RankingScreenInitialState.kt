package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData

val RankingScreenInitialState = StateDescriptionWithData(
    StateDescription.Idle,
    RankingScreenData.NoData
)
