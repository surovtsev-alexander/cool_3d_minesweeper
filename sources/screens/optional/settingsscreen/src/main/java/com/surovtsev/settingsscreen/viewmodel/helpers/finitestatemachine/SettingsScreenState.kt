package com.surovtsev.settingsscreen.viewmodel

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.SettingsScreenData

val SettingsScreenInitialState = StateDescriptionWithData(
    StateDescription.Idle,
    SettingsScreenData.NoData
)
