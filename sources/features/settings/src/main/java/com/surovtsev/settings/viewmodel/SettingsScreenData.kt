package com.surovtsev.settings.viewmodel

import com.surovtsev.core.room.dao.SettingsList
import com.surovtsev.core.room.entities.Settings


sealed class SettingsScreenData {
    object NoData: SettingsScreenData()

    open class SettingsLoaded(
        val settingsList: SettingsList,
    ): SettingsScreenData()

    open class SettingsDataIsSelected(
        settingsLoaded: SettingsLoaded,
        val settingsData: Settings.SettingsData,
        val fromUI: Boolean
    ): SettingsLoaded(
        settingsLoaded.settingsList
    )

    open class SettingsIsSelected(
        settingsDataIsSelected: SettingsDataIsSelected,
        val settingsId: Long,
    ): SettingsDataIsSelected(
        settingsDataIsSelected,
        settingsDataIsSelected.settingsData,
        settingsDataIsSelected.fromUI
    ) {
        constructor(
            settingsLoaded: SettingsLoaded,
            settings: Settings,
        ): this(
            SettingsDataIsSelected(
                settingsLoaded,
                settings.settingsData,
                false
            ),
            settings.id
        )
    }
}