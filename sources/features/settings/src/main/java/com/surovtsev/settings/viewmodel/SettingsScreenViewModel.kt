package com.surovtsev.settings.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.core.viewmodel.CommandProcessor
import com.surovtsev.core.viewmodel.ScreenCommandHandler
import com.surovtsev.core.viewmodel.ScreenState
import com.surovtsev.core.viewmodel.TemplateScreenViewModel
import com.surovtsev.settings.dagger.SettingsComponent
import com.surovtsev.settings.dagger.SettingsComponentEntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

typealias SettingsScreenStateHolder = MutableLiveData<SettingsScreenState>
typealias SettingsScreenStateValue = LiveData<SettingsScreenState>

typealias SettingsScreenCommandHandler = ScreenCommandHandler<CommandFromSettingsScreen>

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    settingsComponentProvider: Provider<SettingsComponent.Builder>,
):
    TemplateScreenViewModel<CommandFromSettingsScreen, SettingsScreenData>(
        CommandFromSettingsScreen.LoadSettings, SettingsScreenData.NoData
    ),
    DefaultLifecycleObserver
{

    private val settingsDao: SettingsDao
    private val saveController: SaveController

    override val stateHolder: SettingsScreenStateHolder
    override val stateValue: SettingsScreenStateValue

    init {
        val settingsComponent: SettingsComponent =
            settingsComponentProvider
                .get()
                .build()
        val settingsComponentEntryPoint: SettingsComponentEntryPoint =
            EntryPoints.get(
                settingsComponent,
                SettingsComponentEntryPoint::class.java
            )

        settingsDao =
            settingsComponentEntryPoint.settingsDao
        saveController =
            settingsComponentEntryPoint.saveController

        stateHolder =
            settingsComponentEntryPoint.settingsScreenStateHolder
        stateValue =
            settingsComponentEntryPoint.settingsScreenStateValue
    }

    override suspend fun getCommandProcessor(command: CommandFromSettingsScreen): CommandProcessor? {
        return when (command) {
            is CommandFromSettingsScreen.CloseError             -> ::closeError
            is CommandFromSettingsScreen.LoadSettings           -> ::loadSettings
            is CommandFromSettingsScreen.LoadSelectedSettings   -> ::loadSelectedSettings
            is CommandFromSettingsScreen.RememberSettings       -> suspend { rememberSettings(command.settings) }
            is CommandFromSettingsScreen.RememberSettingsData   -> suspend { rememberSettingsData(command.settingsData, command.fromSlider) }
            is CommandFromSettingsScreen.ApplySettings          -> ::applySettings
            is CommandFromSettingsScreen.DeleteSettings         -> suspend { deleteSettings(command.settingsId) }
            else                                                -> null
        }

    }

    private suspend fun loadSettings() {
        val settingsList = settingsDao.getAll()

        publishLoadingState(
            SettingsScreenData.SettingsLoaded(
                settingsList
            )
        )

        return handleCommand(
            CommandFromSettingsScreen.LoadSelectedSettings
        )
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
        doActionIfStateIsChildIs<SettingsScreenData.SettingsDataIsSelected>(
            "error while applying settings"
        ) { screenData ->
            val settingsData = screenData.settingsData

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
        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error while deleting settings"
        ) {
            settingsDao.delete(settingsId)

            return handleCommand(
                CommandFromSettingsScreen.LoadSettings
            )
        }
    }

    private suspend fun loadSelectedSettings() {
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
