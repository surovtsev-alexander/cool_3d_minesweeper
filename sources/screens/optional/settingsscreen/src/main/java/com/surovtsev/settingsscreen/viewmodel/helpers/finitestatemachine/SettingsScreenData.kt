package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.core.room.dao.SettingsList
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.finitestatemachine.state.data.InitializationIsNotFinished


sealed interface SettingsScreenData: ScreenData.UserData {

    open class SettingsLoaded(
        val settingsList: SettingsList,
    ): SettingsScreenData, InitializationIsNotFinished

    open class SettingsDataIsSelected(
        settingsLoaded: SettingsLoaded,
        val settingsData: Settings.SettingsData,
        val fromSlider: Boolean
    ): SettingsLoaded(
        settingsLoaded.settingsList
    )

    open class SettingsIsSelected(
        settingsDataIsSelected: SettingsDataIsSelected,
        val settingsId: Long,
    ): SettingsDataIsSelected(
        settingsDataIsSelected,
        settingsDataIsSelected.settingsData,
        settingsDataIsSelected.fromSlider
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