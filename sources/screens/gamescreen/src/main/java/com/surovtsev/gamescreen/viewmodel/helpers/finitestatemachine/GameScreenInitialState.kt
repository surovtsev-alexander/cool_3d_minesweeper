package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData

val GameScreenInitialState = StateDescriptionWithData(
    StateDescription.Idle,
    GameScreenData.NoData
)
