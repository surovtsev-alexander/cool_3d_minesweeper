package com.surovtsev.settings.viewmodel

import com.surovtsev.core.room.entities.Settings

sealed class CommandFromSettingsScreen {
    object CloseError: CommandFromSettingsScreen()

    object LoadSettings: CommandFromSettingsScreen()

    object LoadSelectedSettings: CommandFromSettingsScreen()

    data class RememberSettings(
        val settings: Settings
    ): CommandFromSettingsScreen()

    data class RememberSettingsData(
        val settingsData: Settings.SettingsData
    ): CommandFromSettingsScreen()

    data class DeleteSettings(
        val settingsId: Long
    ): CommandFromSettingsScreen()

    object ApplySettings: CommandFromSettingsScreen()

}