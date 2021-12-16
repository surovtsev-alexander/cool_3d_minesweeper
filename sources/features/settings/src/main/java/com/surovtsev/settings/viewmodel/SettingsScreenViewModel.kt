package com.surovtsev.settings.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.core.viewmodel.ScreenCommandsHandler
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

typealias SettingsScreenCommandsHandler = ScreenCommandsHandler<CommandFromSettingsScreen>

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

    override val dataHolder: MutableLiveData<ScreenState<out SettingsScreenData>>
    override val dataValue: LiveData<ScreenState<out SettingsScreenData>>

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

        dataHolder =
            settingsComponentEntryPoint.settingsScreenStateHolder
        dataValue =
            settingsComponentEntryPoint.settingsScreenStateValue
    }

    override suspend fun onCommand(command: CommandFromSettingsScreen) {
        when (command) {
            is CommandFromSettingsScreen.CloseError             -> closeError()
            is CommandFromSettingsScreen.LoadSettings           -> loadSettings()
            is CommandFromSettingsScreen.LoadSelectedSettings   -> loadSelectedSettings()
            is CommandFromSettingsScreen.RememberSettings       -> rememberSettings(command.settings)
            is CommandFromSettingsScreen.RememberSettingsData   -> rememberSettingsData(command.settingsData, command.fromUI)
            is CommandFromSettingsScreen.ApplySettings          -> applySettings()
            is CommandFromSettingsScreen.DeleteSettings         -> deleteSettings(command.settingsId)
            else                                                -> publishError("error while processing commands")
        }
    }


    private suspend fun loadSettings() {
        val settingsList = settingsDao.getAll()

        publishNewState(
            ScreenState.Loading(
                SettingsScreenData.SettingsLoaded(
                    settingsList
                )
            )
        )

        return handleCommand(
            CommandFromSettingsScreen.LoadSelectedSettings
        )
    }

    private suspend fun rememberSettingsData(
        settingsData: Settings.SettingsData,
        fromUI: Boolean
    ) {
        doActionIfStateIsChildIs<SettingsScreenData.SettingsLoaded>(
            "error while updating settings"
        ) { screenData ->
            publishNewState(
                ScreenState.Idle(
                    SettingsScreenData.SettingsDataIsSelected(
                        screenData,
                        settingsData,
                        fromUI
                    )
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
            publishNewState(
                ScreenState.Idle(
                    SettingsScreenData.SettingsIsSelected(
                        screenData,
                        settings
                    )
                )
            )
        }
    }
}
