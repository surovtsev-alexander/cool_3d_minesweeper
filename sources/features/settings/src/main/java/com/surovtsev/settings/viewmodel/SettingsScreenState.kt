package com.surovtsev.settings.viewmodel

import com.surovtsev.utils.viewmodel.ScreenState

typealias SettingsScreenState = ScreenState<out SettingsScreenData>

val SettingsScreenInitialState = ScreenState.Idle(
    SettingsScreenData.NoData
)
