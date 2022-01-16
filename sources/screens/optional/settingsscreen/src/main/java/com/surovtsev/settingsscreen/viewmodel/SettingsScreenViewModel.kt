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
import com.surovtsev.utils.dagger.componentholder.DaggerComponentHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

typealias SettingsScreenStateFlow = ScreenStateFlow<SettingsScreenData>

typealias SettingsScreenEventHandler = EventHandler<EventToSettingsScreenViewModel>

typealias SettingsScreenErrorDialogPlacer = ErrorDialogPlacer<EventToSettingsScreenViewModel, SettingsScreenData>

class SettingsScreenViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted appComponentEntryPoint: AppComponentEntryPoint,
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

    private val settingsComponentHolder = DaggerComponentHolder<SettingsComponent> {
        DaggerSettingsComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .build()
    }


    override suspend fun getEventProcessor(event: EventToSettingsScreenViewModel): EventProcessor<EventToSettingsScreenViewModel>? {
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

    private suspend fun triggerInitialization(): EventProcessingResult<EventToSettingsScreenViewModel> {
        val currSettingsComponent =
            settingsComponentHolder
                .getOrCreate()

        prepopulateSettingsTableWithDefaultValues(
            currSettingsComponent.settingsDao
        )

        return EventProcessingResult.PushNewEvent(
            EventToSettingsScreenViewModel.LoadSettingsList
        )
    }

    private suspend fun loadSettingsList(): EventProcessingResult<EventToSettingsScreenViewModel> {
        val currSettingsComponent = settingsComponentHolder.component

        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error while loading settings list"
            )
            return EventProcessingResult.Processed()
        }

        val settingsList = currSettingsComponent.settingsDao.getAll()

        stateHolder.publishLoadingState(
            SettingsScreenData.SettingsLoaded(
                settingsList
            )
        )

        return EventProcessingResult.PushNewEvent(
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
    ): EventProcessingResult<EventToSettingsScreenViewModel> {
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
        return EventProcessingResult.Processed()
    }

    private suspend fun applySettings(
    ): EventProcessingResult<EventToSettingsScreenViewModel> {
        val currSettingsComponent = settingsComponentHolder.component

        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error (1) while applying settings"
            )
            return EventProcessingResult.Processed()
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

        return EventProcessingResult.Processed()
    }

    private suspend fun deleteSettings(
        settingsId: Long
    ): EventProcessingResult<EventToSettingsScreenViewModel> {
        val currSettingsComponent = settingsComponentHolder.component
        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error (1) while deleting settings"
            )
            return EventProcessingResult.Processed()
        }

        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error (2) while deleting settings"
        ) {
            currSettingsComponent.settingsDao.delete(settingsId)

            return@deleteSettings EventProcessingResult.PushNewEvent(
                EventToSettingsScreenViewModel.LoadSettingsList
            )
        }
        return EventProcessingResult.Processed()
    }

    private suspend fun loadSelectedSettings(): EventProcessingResult<EventToSettingsScreenViewModel> {
        val currSettingsComponent = settingsComponentHolder.component

        if (currSettingsComponent == null) {
            stateHolder.publishErrorState(
                "error while loading selected settings"
            )

            return EventProcessingResult.Processed()
        }

        val saveController = currSettingsComponent.saveController
        val settingsDao = currSettingsComponent.settingsDao

        val selectedSettingsData = saveController.loadSettingDataOrDefault()

        val selectedSettings = settingsDao.getBySettingsData(
            selectedSettingsData
        )?: Settings(selectedSettingsData, -1)

        rememberSettings(selectedSettings)

        return EventProcessingResult.Processed()
    }

    private suspend fun rememberSettings(
        settings: Settings,
    ): EventProcessingResult<EventToSettingsScreenViewModel> {
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
        return EventProcessingResult.Processed()
    }
}
