package com.surovtsev.settings.viewmodel

import com.surovtsev.core.room.dao.SettingsList
import com.surovtsev.core.room.entities.Settings


sealed class SettingsScreenData {
    object NoData: SettingsScreenData()

    open class SettingsLoaded(
        val settingsList: SettingsList,
    ): SettingsScreenData()

    open class SelectedSettings(
        settingsLoaded: SettingsLoaded,
        val settingsData: Settings.SettingsData
    ): SettingsLoaded(
        settingsLoaded.settingsList
    )

    open class SelectedSettingsWithId(
        selectedSettings: SelectedSettings,
        val settingsId: Long,
    ): SelectedSettings(
        selectedSettings,
        selectedSettings.settingsData
    ) {
        constructor(
            settingsLoaded: SettingsLoaded,
            settings: Settings
        ): this(
            SelectedSettings(
                settingsLoaded,
                settings.settingsData
            ),
            settings.id
        )
    }
}