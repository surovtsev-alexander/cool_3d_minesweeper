package com.surovtsev.gamescreen.viewmodel

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateWithData

val GameScreenInitialState = StateWithData(
    State.Idle,
    GameScreenData.NoData
)