package com.surovtsev.settingsscreen.viewmodel

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.core.viewmodel.*
import com.surovtsev.settingsscreen.dagger.DaggerSettingsComponent
import com.surovtsev.settingsscreen.dagger.SettingsComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

typealias SettingsScreenStateFlow = ScreenStateFlow<SettingsScreenData>

typealias SettingsScreenEventHandler = EventHandler<EventToSettingsScreenViewModel>

typealias SettingsScreenErrorDialogPlacer = ErrorDialogPlacer<EventToSettingsScreenViewModel, SettingsScreenData>

class SettingsScreenViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel<EventToSettingsScreenViewModel, SettingsScreenData>(
        EventToSettingsScreenViewModel.MandatoryEvents,
        SettingsScreenData.NoData,
        SettingsScreenInitialState,
    ),
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<SettingsScreenViewModel>

    private var settingsComponent: SettingsComponent? = null

    override suspend fun getEventProcessor(event: EventToSettingsScreenViewModel): EventProcessor? {
        return when (event) {
            is EventToSettingsScreenViewModel.HandleLeavingScreen    -> suspend { handleScreenLeaving(event.owner) }
            is EventToSettingsScreenViewModel.CloseError             -> ::closeError
            is EventToSettingsScreenViewModel.CloseErrorAndFinish    -> ::closeError
            is EventToSettingsScreenViewModel.TriggerInitialization  -> ::triggerInitialization
            is EventToSettingsScreenViewModel.LoadSettingsList       -> ::loadSettingsList
            is EventToSettingsScreenViewModel.LoadSelectedSettings   -> ::loadSelectedSettings
            is EventToSettingsScreenViewModel.RememberSettings       -> suspend { rememberSettings(event.settings) }
            is EventToSettingsScreenViewModel.RememberSettingsData   -> suspend { rememberSettingsData(event.settingsData, event.fromSlider) }
            is EventToSettingsScreenViewModel.ApplySettings          -> ::applySettings
            is EventToSettingsScreenViewModel.DeleteSettings         -> suspend { deleteSettings(event.settingsId) }
            else                                                -> null
        }
    }

    private suspend fun triggerInitialization() {
        val currSettingsComponent: SettingsComponent

        settingsComponent.let {
            if (it == null) {
                currSettingsComponent = DaggerSettingsComponent
                    .builder()
                    .appComponentEntryPoint(appComponentEntryPoint)
                    .build()
                    .apply {
                        settingsComponent = this
                    }
            } else {
                currSettingsComponent = it
            }
        }

        prepopulateSettingsTableWithDefaultValues(
            currSettingsComponent.settingsDao
        )

        return handleEvent(
            EventToSettingsScreenViewModel.LoadSettingsList
        )
    }

    private suspend fun loadSettingsList() {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null) {
            publishErrorState(
                "error while loading settingsscreen list"
            )
            return
        }

        val settingsList = currSettingsComponent.settingsDao.getAll()

        publishLoadingState(
            SettingsScreenData.SettingsLoaded(
                settingsList
            )
        )

        return handleEvent(
            EventToSettingsScreenViewModel.LoadSelectedSettings
        )
    }

    private fun prepopulateSettingsTableWithDefaultValues(
        settingsDao: SettingsDao
    ) {
        val needToPrepopulate = settingsDao.getCount() == 0
        if (!needToPrepopulate) {
            return
        }

        // TODO: 01.01.2022 move to SettingsDao
        val dataToPrepopulate = arrayOf(
            12 to 20,
            10 to 20,
            8  to 16,
            5  to 12,
            12 to 30,
            12 to 25,
            10 to 18,
        )

        dataToPrepopulate.forEach {
            settingsDao.insert(
                Settings(
                    Settings.SettingsData(
                        it.first,
                        it.second
                    )
                )
            )
        }
    }

    private suspend fun rememberSettingsData(
        settingsData: Settings.SettingsData,
        fromSlider: Boolean
    ) {
        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error while updating settingsscreen"
        ) { screenData ->
            publishIdleState(
                SettingsScreenData.SettingsDataIsSelected(
                    screenData,
                    settingsData,
                    fromSlider
                )
            )
        }
    }

    private suspend fun applySettings() {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null) {
            publishErrorState(
                "error (1) while applying settings"
            )
            return
        }

        doActionIfStateIsChildIs<SettingsScreenData.SettingsDataIsSelected>(
            "error (2) while applying settingsscreen"
        ) { screenData ->
            val settingsData = screenData.settingsData

            val settingsDao = currSettingsComponent.settingsDao
            val saveController = currSettingsComponent.saveController

            settingsDao.getOrCreate(
                settingsData
            )

            saveController.save(
                SaveTypes.GameSettingsJson,
                settingsData
            )

            withUIContext {
                finishAction?.invoke()
            }
        }
    }

    private suspend fun deleteSettings(
        settingsId: Long
    ) {
        val currSettingsComponent = settingsComponent
        if (currSettingsComponent == null) {
            publishErrorState(
                "error (1) while deleting settingsscreen"
            )
            return
        }

        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error (2) while deleting settingsscreen"
        ) {
            currSettingsComponent.settingsDao.delete(settingsId)

            return handleEvent(
                EventToSettingsScreenViewModel.LoadSettingsList
            )
        }
    }

    private suspend fun loadSelectedSettings() {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null) {
            publishErrorState(
                "error while loading selected settingsscreen"
            )

            return
        }

        val saveController = currSettingsComponent.saveController
        val settingsDao = currSettingsComponent.settingsDao

        val selectedSettingsData = saveController.loadSettingDataOrDefault()

        val selectedSettings = settingsDao.getBySettingsData(
            selectedSettingsData
        )?: Settings(selectedSettingsData, -1)

        rememberSettings(selectedSettings)
    }

    private suspend fun rememberSettings(
        settings: Settings,
    ) {
        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "internal error: can not select settings"
        ) { screenData ->
            publishIdleState(
                SettingsScreenData.SettingsIsSelected(
                    screenData,
                    settings
                )
            )
        }
    }
}
