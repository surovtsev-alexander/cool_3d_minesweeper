package com.surovtsev.settingsscreen.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.viewmodel.CommandFromScreen

sealed class CommandFromSettingsScreen(
    override val setLoadingStateWhileProcessing: Boolean = true
): CommandFromScreen {
    class HandleLeavingScreen(
        override val owner: LifecycleOwner
    ):
        CommandFromSettingsScreen(),
        CommandFromScreen.HandleScreenLeaving

    object CloseError: CommandFromSettingsScreen(), CommandFromScreen.CloseError

    object CloseErrorAndFinish: CommandFromSettingsScreen(), CommandFromScreen.CloseErrorAndFinish

    object TriggerInitialization: CommandFromSettingsScreen(), CommandFromScreen.Init

    object LoadSettingsList: CommandFromSettingsScreen()

    object LoadSelectedSettings: CommandFromSettingsScreen()

    data class RememberSettings(
        val settings: Settings
    ): CommandFromSettingsScreen()

    data class RememberSettingsData(
        val settingsData: Settings.SettingsData,
        val fromSlider: Boolean = false
    ): CommandFromSettingsScreen()

    data class DeleteSettings(
        val settingsId: Long
    ): CommandFromSettingsScreen()

    object ApplySettings: CommandFromSettingsScreen()

    object Finish: CommandFromSettingsScreen(), CommandFromScreen.Finish

    object BaseCommands: CommandFromScreen.BaseCommands<CommandFromSettingsScreen>(
        TriggerInitialization,
        CloseError,
        CloseErrorAndFinish,
        { HandleLeavingScreen(it) },
    )
}