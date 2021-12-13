package com.surovtsev.settings.viewmodel

import com.surovtsev.utils.viewmodel.ScreenState

typealias SettingsScreenState = ScreenState<SettingsScreenData>

val SettingsScreenInitialState = ScreenState.Idle(
    SettingsScreenData.NoData
)
