/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toNormalPriorityEventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.toIdle
import com.surovtsev.finitestatemachine.state.toLoading
import com.surovtsev.settingsscreen.dagger.SettingsScreenScope
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.EventToSettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.SettingsScreenData
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.templateviewmodel.finitestatemachine.screendata.ViewModelData
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
            is EventToSettingsScreenViewModel.RememberSettingsData   -> suspend { rememberSettingsData(event.settingsData) }
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

        val newState = eventHandlerParameters.fsmStateFlow.value.toLoading(
            SettingsScreenData.SettingsLoadedData(
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
    ): EventProcessingResult {
        return calculateEventResultProcessingIsState<SettingsScreenData.SettingsLoaded>(
            "error while updating settings"
        ) { screenData ->
            EventProcessingResult.Ok(
                newState = eventHandlerParameters.fsmStateFlow.value.toIdle(
                    SettingsScreenData.SettingsDataIsSelected(
                        screenData,
                        settingsData
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
            val settingsData = screenData.uiControls.getSettingsData()

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

        return rememberSettingsData(selectedSettings.settingsData)
    }

    private inline fun <reified T: ViewModelData> calculateEventResultProcessingIsState(
        errorMessage: String, action: (screenData: T) -> EventProcessingResult
    ): EventProcessingResult {
        val state = eventHandlerParameters.fsmStateFlow.value
        val screenData = state.data

        return if (screenData !is T) {
            EventProcessingResult.Error(
                errorMessage
            )
        } else {
            action.invoke(screenData)
        }
    }
}
