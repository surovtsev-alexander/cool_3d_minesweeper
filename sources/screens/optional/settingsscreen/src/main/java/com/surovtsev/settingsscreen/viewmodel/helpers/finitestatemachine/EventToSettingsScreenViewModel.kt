package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.core.room.entities.Settings
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel

sealed class EventToSettingsScreenViewModel(
): EventToViewModel.UserEvent() {
    object LoadSettingsList: EventToSettingsScreenViewModel()

    object LoadSelectedSettings: EventToSettingsScreenViewModel()

    data class RememberSettings(
        val settings: Settings
    ): EventToSettingsScreenViewModel()

    data class RememberSettingsData(
        val settingsData: Settings.SettingsData,
        val fromSlider: Boolean = false
    ): EventToSettingsScreenViewModel()

    data class DeleteSettings(
        val settingsId: Long
    ): EventToSettingsScreenViewModel()

    object ApplySettings: EventToSettingsScreenViewModel()
}
