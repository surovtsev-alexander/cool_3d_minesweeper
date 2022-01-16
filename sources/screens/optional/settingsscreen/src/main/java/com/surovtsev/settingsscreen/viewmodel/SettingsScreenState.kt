package com.surovtsev.settingsscreen.viewmodel

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData

val SettingsScreenInitialState = StateDescriptionWithData(
    StateDescription.Idle,
    SettingsScreenData.NoData
)
