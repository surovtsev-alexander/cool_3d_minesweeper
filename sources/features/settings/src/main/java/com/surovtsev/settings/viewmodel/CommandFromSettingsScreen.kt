package com.surovtsev.settings.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.viewmodel.CommandFromScreen

sealed interface CommandFromSettingsScreen: CommandFromScreen {
    class HandleLeavingScreen(owner: LifecycleOwner):
        CommandFromSettingsScreen,
        CommandFromScreen.HandleScreenLeaving(owner)

    object CloseError: CommandFromSettingsScreen

    object LoadSettings: CommandFromSettingsScreen

    object LoadSelectedSettings: CommandFromSettingsScreen

    data class RememberSettings(
        val settings: Settings
    ): CommandFromSettingsScreen

    data class RememberSettingsData(
        val settingsData: Settings.SettingsData,
        val fromSlider: Boolean = false
    ): CommandFromSettingsScreen

    data class DeleteSettings(
        val settingsId: Long
    ): CommandFromSettingsScreen

    object ApplySettings: CommandFromSettingsScreen
}