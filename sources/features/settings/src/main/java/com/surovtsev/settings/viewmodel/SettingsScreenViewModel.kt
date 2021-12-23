package com.surovtsev.settings.viewmodel

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.core.viewmodel.CommandFromScreen
import com.surovtsev.core.viewmodel.CommandProcessor
import com.surovtsev.core.viewmodel.ScreenCommandHandler
import com.surovtsev.core.viewmodel.TemplateScreenViewModel
import com.surovtsev.settings.dagger.DaggerSettingsComponent
import com.surovtsev.settings.dagger.SettingsComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

typealias SettingsScreenStateHolder = MutableLiveData<SettingsScreenState>
typealias SettingsScreenStateValue = LiveData<SettingsScreenState>

typealias SettingsScreenCommandHandler = ScreenCommandHandler<CommandFromSettingsScreen>

class SettingsScreenViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel<CommandFromSettingsScreen, SettingsScreenData>(
        CommandFromScreen.BaseCommands(
            CommandFromSettingsScreen.TriggerInitialization,
            CommandFromSettingsScreen.CloseError,
            CommandFromSettingsScreen.CloseErrorAndFinish,
        ) { CommandFromSettingsScreen.HandleLeavingScreen(it) },
        SettingsScreenData.NoData,
        SettingsScreenStateHolder(SettingsScreenInitialState),
    ),
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<SettingsScreenViewModel>

    private var settingsComponent: SettingsComponent? = null

    override suspend fun getCommandProcessor(command: CommandFromSettingsScreen): CommandProcessor? {
        return when (command) {
            is CommandFromSettingsScreen.HandleLeavingScreen    -> suspend { handleScreenLeaving(command.owner) }
            is CommandFromSettingsScreen.CloseError             -> ::closeError
            is CommandFromSettingsScreen.CloseErrorAndFinish    -> ::closeError
            is CommandFromSettingsScreen.TriggerInitialization  -> ::triggerInitialization
            is CommandFromSettingsScreen.LoadSettingsList       -> ::loadSettingsList
            is CommandFromSettingsScreen.LoadSelectedSettings   -> ::loadSelectedSettings
            is CommandFromSettingsScreen.RememberSettings       -> suspend { rememberSettings(command.settings) }
            is CommandFromSettingsScreen.RememberSettingsData   -> suspend { rememberSettingsData(command.settingsData, command.fromSlider) }
            is CommandFromSettingsScreen.ApplySettings          -> ::applySettings
            is CommandFromSettingsScreen.DeleteSettings         -> suspend { deleteSettings(command.settingsId) }
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

        return handleCommand(
            CommandFromSettingsScreen.LoadSettingsList
        )
    }

    private suspend fun loadSettingsList() {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null || true) {
            publishErrorState(
                "error while loading settings list"
            )
            return
        }

        val settingsList = currSettingsComponent.settingsDao.getAll()

        publishLoadingState(
            SettingsScreenData.SettingsLoaded(
                settingsList
            )
        )

        return handleCommand(
            CommandFromSettingsScreen.LoadSelectedSettings
        )
    }

    private fun prepopulateSettingsTableWithDefaultValues(
        settingsDao: SettingsDao
    ) {
        val needToPrepopulate = settingsDao.getCount() == 0
        if (!needToPrepopulate) {
            return
        }

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
            "error while updating settings"
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
            "error (2) while applying settings"
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
                "error (1) while deleting settings"
            )
            return
        }

        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error (2) while deleting settings"
        ) {
            currSettingsComponent.settingsDao.delete(settingsId)

            return handleCommand(
                CommandFromSettingsScreen.LoadSettingsList
            )
        }
    }

    private suspend fun loadSelectedSettings() {
        val currSettingsComponent = settingsComponent

        if (currSettingsComponent == null) {
            publishErrorState(
                "error while loading selected settings"
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
