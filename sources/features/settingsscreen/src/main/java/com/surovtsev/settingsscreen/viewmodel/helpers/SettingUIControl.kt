package com.surovtsev.settingsscreen.viewmodel.helpers

import com.surovtsev.core.room.entities.Settings
import com.surovtsev.utils.compose.components.SliderPositionData

typealias ValueCalculator = (settingsData: Settings.SettingsData) -> Int
typealias SettingsDataCalculator = (settingsData: Settings.SettingsData, newValue: Int) -> Settings.SettingsData

data class SettingUIControl(
    val title: String,
    val borders: IntRange,
    val sliderPositionData: SliderPositionData,
    val valueCalculator: ValueCalculator,
    val settingsDataCalculator: SettingsDataCalculator
)
