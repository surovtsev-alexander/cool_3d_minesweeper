package com.surovtsev.gamescreen.viewmodel

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData

val GameScreenInitialState = StateDescriptionWithData(
    StateDescription.Idle,
    GameScreenData.NoData
)