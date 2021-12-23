package com.surovtsev.settingsscreen.viewmodel

import com.surovtsev.core.viewmodel.ScreenState

typealias SettingsScreenState = ScreenState<out SettingsScreenData>

val SettingsScreenInitialState = ScreenState.Idle(
    SettingsScreenData.NoData
)
