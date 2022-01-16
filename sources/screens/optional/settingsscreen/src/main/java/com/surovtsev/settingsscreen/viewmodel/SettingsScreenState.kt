package com.surovtsev.settingsscreen.viewmodel

import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateWithData

val SettingsScreenInitialState = StateWithData(
    State.Idle,
    SettingsScreenData.NoData
)
