package com.surovtsev.settingsscreen.viewmodel

import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.SettingsScreenData

val SettingsScreenInitialState = State(
    Description.Idle,
    SettingsScreenData.NoData
)
