package com.surovtsev.settingsscreen.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.viewmodel.EventToViewModel

sealed class EventToSettingsScreenViewModel(
    override val doNotPushToQueue: Boolean = false,
    override val pushToHead: Boolean = false,
    override val setLoadingStateBeforeProcessing: Boolean = true
): EventToViewModel {
    class HandleLeavingScreen(
        override val owner: LifecycleOwner
    ):
        EventToSettingsScreenViewModel(),
        EventToViewModel.HandleScreenLeaving

    object CloseError: EventToSettingsScreenViewModel(), EventToViewModel.CloseError

    object CloseErrorAndFinish: EventToSettingsScreenViewModel(), EventToViewModel.CloseErrorAndFinish

    object TriggerInitialization: EventToSettingsScreenViewModel(), EventToViewModel.Init

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

    object Finish: EventToSettingsScreenViewModel(), EventToViewModel.Finish

    object MandatoryEvents: EventToViewModel.MandatoryEvents<EventToSettingsScreenViewModel>(
        TriggerInitialization,
        CloseError,
        CloseErrorAndFinish,
        { HandleLeavingScreen(it) },
    )
}