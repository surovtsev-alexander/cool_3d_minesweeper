package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.finitestatemachine.state.State

val GameScreenInitialState = State(
    Description.Idle,
    GameScreenData.NoData
)
