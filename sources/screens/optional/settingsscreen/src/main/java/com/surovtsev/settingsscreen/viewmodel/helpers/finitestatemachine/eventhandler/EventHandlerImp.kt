package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.templateviewmodel.finitestatemachine.screendata.ViewModelData
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toNormalPriorityEventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.settingsscreen.dagger.SettingsScreenScope
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.EventToSettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.SettingsScreenData
import javax.inject.Inject

@SettingsScreenScope
class EventHandlerImp @Inject constructor(
    private val eventHandlerParameters: EventHandlerParameters,
): EventHandler {

    override val transitions: List<EventHandler.Transition> = emptyList()

    override fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult {
        val eventProcessorAction = when (event) {
            is EventToViewModel.Init                                 -> ::triggerInitialization
            is EventToSettingsScreenViewModel.LoadSettingsList       -> ::loadSettingsList
            is EventToSettingsScreenViewModel.LoadSelectedSettings   -> ::loadSelectedSettings
            is EventToSettingsScreenViewModel.RememberSettings       -> suspend { rememberSettings(event.settings) }
            is EventToSettingsScreenViewModel.RememberSettingsData   -> suspend { rememberSettingsData(event.settingsData, event.fromSlider) }
            is EventToSettingsScreenViewModel.ApplySettings          -> ::applySettings
            is EventToSettingsScreenViewModel.DeleteSettings         -> suspend { deleteSettings(event.settingsId) }
            else                                                     -> null
        }
        
        return EventHandlingResult.GeneratorHelper.processOrSkipIfNull(
            eventProcessorAction.toNormalPriorityEventProcessor()
        )
    }


    private suspend fun triggerInitialization(): EventProcessingResult {
        prepopulateSettingsTableWithDefaultValues(
            eventHandlerParameters.settingsDao
        )

        return EventProcessingResult.Ok(
            EventToSettingsScreenViewModel.LoadSettingsList
        )
    }

    private suspend fun loadSettingsList(): EventProcessingResult {
        val settingsList = eventHandlerParameters.settingsDao.getAll()

        val newState = eventHandlerParameters.stateHolder.toLoadingState(
            SettingsScreenData.SettingsLoaded(
                settingsList
            )
        )

        return EventProcessingResult.Ok(
            EventToSettingsScreenViewModel.LoadSelectedSettings,
            newState
        )
    }

    private fun prepopulateSettingsTableWithDefaultValues(
        settingsDao: SettingsDao
    ) {
        val needToPrepopulate = settingsDao.getCount() == 0

        if (needToPrepopulate) {
            settingsDao.prepopulate()
        }
    }

    private suspend fun rememberSettingsData(
        settingsData: Settings.SettingsData,
        fromSlider: Boolean
    ): EventProcessingResult {
        return calculateEventResultProcessingIsState<SettingsScreenData.SettingsLoaded>(
            "error while updating settings"
        ) { screenData ->
            EventProcessingResult.Ok(
                newState = eventHandlerParameters.stateHolder.toIdleState(
                    SettingsScreenData.SettingsDataIsSelected(
                        screenData,
                        settingsData,
                        fromSlider
                    )
                )
            )
        }
    }

    private suspend fun applySettings(
    ): EventProcessingResult {
        return calculateEventResultProcessingIsState<SettingsScreenData.SettingsDataIsSelected>(
            "error while applying settings"
        ) { screenData ->
            val settingsData = screenData.settingsData

            val settingsDao = eventHandlerParameters.settingsDao
            val saveController = eventHandlerParameters.saveController

            settingsDao.getOrCreate(
                settingsData
            )

            saveController.save(
                SaveTypes.GameSettingsJson,
                settingsData
            )

            EventProcessingResult.Ok(
                EventToViewModel.Finish
            )
        }
    }

    private suspend fun deleteSettings(
        settingsId: Long
    ): EventProcessingResult {
        return calculateEventResultProcessingIsState<SettingsScreenData.SettingsLoaded>(
            "error while deleting settings"
        ) {
            eventHandlerParameters.settingsDao.delete(settingsId)

            EventProcessingResult.Ok(
                EventToSettingsScreenViewModel.LoadSettingsList
            )
        }
    }

    private suspend fun loadSelectedSettings(): EventProcessingResult {
        val saveController = eventHandlerParameters.saveController
        val settingsDao = eventHandlerParameters.settingsDao

        val selectedSettingsData = saveController.loadSettingDataOrDefault()

        val selectedSettings = settingsDao.getBySettingsData(
            selectedSettingsData
        )?: Settings(selectedSettingsData, -1)

        return rememberSettings(selectedSettings)
    }

    private suspend fun rememberSettings(
        settings: Settings,
    ): EventProcessingResult {
        return calculateEventResultProcessingIsState<SettingsScreenData.SettingsLoaded>(
            "internal error: can not select settings"
        ) { screenData ->
            EventProcessingResult.Ok(
                newState = eventHandlerParameters.stateHolder.toIdleState(
                    SettingsScreenData.SettingsIsSelected(
                        screenData,
                        settings,
                    )
                )
            )
        }
    }

    private inline fun <reified T: ViewModelData> calculateEventResultProcessingIsState(
        errorMessage: String, action: (screenData: T) -> EventProcessingResult
    ): EventProcessingResult {
        val stateHolder = eventHandlerParameters.stateHolder
        val screenData = stateHolder.state.value.data

        return if (screenData !is T) {
            EventProcessingResult.Error(
                errorMessage
            )
        } else {
            action.invoke(screenData)
        }
    }
}
