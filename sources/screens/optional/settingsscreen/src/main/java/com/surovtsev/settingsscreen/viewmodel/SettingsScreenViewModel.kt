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
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessingResult
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

    private suspend fun triggerInitialization(): EventProcessingResult {
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

        handleEvent(
            EventToSettingsScreenViewModel.LoadSettingsList
        )
        return EventProcessingResult.Processed
    }

    private suspend fun loadSettingsList(): EventProcessingResult {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error while loading settings list"
            )
            return EventProcessingResult.Processed
        }

        val settingsList = currSettingsComponent.settingsDao.getAll()

        stateHolder.publishLoadingState(
            SettingsScreenData.SettingsLoaded(
                settingsList
            )
        )

        handleEvent(
            EventToSettingsScreenViewModel.LoadSelectedSettings
        )
        return EventProcessingResult.Processed
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
    ): EventProcessingResult {
        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error while updating settings"
        ) { screenData ->
            stateHolder.publishIdleState(
                SettingsScreenData.SettingsDataIsSelected(
                    screenData,
                    settingsData,
                    fromSlider
                )
            )
        }
        return EventProcessingResult.Processed
    }

    private suspend fun applySettings(
    ): EventProcessingResult {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error (1) while applying settings"
            )
            return EventProcessingResult.Processed
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

        return EventProcessingResult.Processed
    }

    private suspend fun deleteSettings(
        settingsId: Long
    ): EventProcessingResult {
        val currSettingsComponent = settingsComponent
        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error (1) while deleting settings"
            )
            return EventProcessingResult.Processed
        }

        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error (2) while deleting settingsscreen"
        ) {
            currSettingsComponent.settingsDao.delete(settingsId)

            handleEvent(
                EventToSettingsScreenViewModel.LoadSettingsList
            )
        }
        return EventProcessingResult.Processed
    }

    private suspend fun loadSelectedSettings(): EventProcessingResult {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error while loading selected settings"
            )

            return EventProcessingResult.Processed
        }

        val saveController = currSettingsComponent.saveController
        val settingsDao = currSettingsComponent.settingsDao

        val selectedSettingsData = saveController.loadSettingDataOrDefault()

        val selectedSettings = settingsDao.getBySettingsData(
            selectedSettingsData
        )?: Settings(selectedSettingsData, -1)

        rememberSettings(selectedSettings)

        return EventProcessingResult.Processed
    }

    private suspend fun rememberSettings(
        settings: Settings,
    ): EventProcessingResult {
        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "internal error: can not select settings"
        ) { screenData ->
            stateHolder.publishIdleState(
                SettingsScreenData.SettingsIsSelected(
                    screenData,
                    settings
                )
            )
        }
        return EventProcessingResult.Processed
    }
}
