package com.surovtsev.settings.viewmodel.helpers

import com.surovtsev.core.room.entities.Settings

typealias ValueCalculator = (settingsData: Settings.SettingsData) -> Int
typealias SettingsDataCalculator = (settingsData: Settings.SettingsData, newValue: Int) -> Settings.SettingsData

data class SettingUIControl(
    val title: String,
    val borders: IntRange,
    val valueCalculator: ValueCalculator,
    val settingsDataCalculator: SettingsDataCalculator
)