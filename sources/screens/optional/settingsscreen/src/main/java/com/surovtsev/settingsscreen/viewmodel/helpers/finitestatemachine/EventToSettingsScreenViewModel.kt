package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.InitEvent

sealed class EventToSettingsScreenViewModel(
): EventToViewModel.UserEvent() {
    object TriggerInitialization: EventToSettingsScreenViewModel(), InitEvent

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

    object MandatoryEvents: EventToViewModel.MandatoryEvents(
        TriggerInitialization,
    )
}