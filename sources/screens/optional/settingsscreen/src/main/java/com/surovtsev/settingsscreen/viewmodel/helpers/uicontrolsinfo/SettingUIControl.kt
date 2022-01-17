package com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo

import com.surovtsev.core.room.entities.Settings
import com.surovtsev.utils.compose.components.SliderPositionMutableStateFlow

typealias ValueCalculator = (settingsData: Settings.SettingsData) -> Int
typealias SettingsDataCalculator = (settingsData: Settings.SettingsData, newValue: Int) -> Settings.SettingsData

data class SettingUIControl(
    val title: String,
    val borders: IntRange,
    val sliderPositionMutableStateFlow: SliderPositionMutableStateFlow,
    val valueCalculator: ValueCalculator,
    val settingsDataCalculator: SettingsDataCalculator
)
