package com.surovtsev.settings.viewmodel

import androidx.lifecycle.*
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.room.entities.SettingsDataFactory
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.core.viewmodel.ScreenCommandsHandler
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.settings.dagger.SettingsComponent
import com.surovtsev.settings.dagger.SettingsComponentEntryPoint
import com.surovtsev.settings.viewmodel.helpers.SettingsScreenControls
import com.surovtsev.settings.viewmodel.helpers.SettingsScreenEvents
import com.surovtsev.settings.viewmodel.helpers.SlidersWithNames
import com.surovtsev.utils.viewmodel.ScreenState
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
    ViewModel(),
    SettingsScreenCommandsHandler,
    DefaultLifecycleObserver,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{

    private val slidersWithNames: SlidersWithNames
    val settingsScreenControls: SettingsScreenControls
    private val settingsDao: SettingsDao
    private val saveController: SaveController
    val settingsScreenEvents: SettingsScreenEvents
    val settingsDataFactory: SettingsDataFactory

    private val settingsScreenStateHolder: SettingsScreenStateHolder
    val settingsScreenStateValue: SettingsScreenStateValue

    var finishAction: (() -> Unit)? = null

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

        slidersWithNames =
            settingsComponentEntryPoint.slidersWithNames
        settingsScreenControls =
            settingsComponentEntryPoint.settingsScreenComponent
        settingsDao =
            settingsComponentEntryPoint.settingsDao
        saveController =
            settingsComponentEntryPoint.saveController
        settingsScreenEvents =
            settingsComponentEntryPoint.settingsScreenEvents
        settingsDataFactory =
            settingsComponentEntryPoint.settingsDataFactory

        settingsScreenStateHolder =
            settingsComponentEntryPoint.settingsScreenStateHolder

        settingsScreenStateValue =
            settingsComponentEntryPoint.settingsScreenStateValue
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        loadDataOld()
    }

    override fun handleCommand(command: CommandFromSettingsScreen) {
        launchOnIOThread {
            setLoadingState()

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
    }

    private suspend fun setLoadingState() {
        publishNewState(
            ScreenState.Loading(
                getCurrentSettingsScreenStateOrDefault()
            )
        )
    }

    private fun getCurrentSettingsScreenStateOrDefault(): SettingsScreenData {
        return settingsScreenStateHolder.value?.screenData ?: SettingsScreenData.NoData
    }

    private suspend fun publishNewState(
        settingsScreenState: SettingsScreenState
    ) {
        withUIContext {
            settingsScreenStateHolder.value = settingsScreenState
        }
    }

    private suspend fun publishError(
        message: String
    ) {
        publishNewState(
            ScreenState.Error(
                getCurrentSettingsScreenStateOrDefault(),
                message
            )
        )
    }

    private suspend fun closeError() {
        publishNewState(
            ScreenState.Idle(
                getCurrentSettingsScreenStateOrDefault()
            )
        )
    }

    private suspend fun loadSettings() {
        val settingsList = settingsDao.getAll()

        publishNewState(
            ScreenState.Idle(
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
        doActionIfStateIsChildOf<SettingsScreenData.SettingsLoaded>(
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
        doActionIfStateIsChildOf<SettingsScreenData.SettingsDataIsSelected>(
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
        doActionIfStateIsChildOf<SettingsScreenData.SettingsLoaded>(
            "error while deleting settings"
        ) { screenData ->
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
        doActionIfStateIsChildOf<SettingsScreenData.SettingsLoaded>(
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

    private suspend inline fun <reified T: SettingsScreenData> doActionIfStateIsChildOf(
        errorMessage: String, action: (screenData: T) -> Unit
    ) {
        val screenData = settingsScreenStateHolder.value?.screenData

        if (screenData == null || screenData !is T) {
            publishError(errorMessage)
        } else {
            action.invoke(screenData)
        }
    }

    private suspend fun reloadSettingsList() {
        val settingsList = settingsDao.getAll()

        withUIContext {
            settingsScreenEvents.settingsListData.onDataChanged(
                settingsList
            )
        }
    }

    private fun loadDataOld() {
        launchOnIOThread {
            reloadSettingsList()

            val loadedSettingsData = saveController.loadSettingDataOrDefault()
            withUIContext {
                setControlValues(loadedSettingsData)
            }

            settingsDao.getBySettingsData(
                loadedSettingsData
            )?.let {
                withUIContext {
                    settingsScreenControls.selectedSettingsId.onDataChanged(it.id)
                }
            }
        }
    }

    fun applySettingsOld() {
        launchOnIOThread {
            val settingsData = settingsDataFactory()
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

    private fun setControlValues(settingsData: Settings.SettingsData) {
        settingsScreenControls.slidersWithNames.let {
            it[Settings.SettingsData.Dimensions.ColumnNames.xCount]!!.onDataChanged(
                settingsData.dimensions.x)
            it[Settings.SettingsData.Dimensions.ColumnNames.yCount]!!.onDataChanged(
                settingsData.dimensions.y)
            it[Settings.SettingsData.Dimensions.ColumnNames.zCount]!!.onDataChanged(
                settingsData.dimensions.z)
            it[Settings.SettingsData.ColumnNames.bombsPercentage]!!.onDataChanged(
                settingsData.bombsPercentage)

        }
    }

    fun selectSettingsOld(settings: Settings) {
        settingsScreenControls.selectedSettingsId.onDataChanged(settings.id)
        setControlValues(settings.settingsData)
    }

    fun deleteSettingsOld(settingsId: Long) {
        launchOnIOThread {
            settingsDao.delete(settingsId)

            reloadSettingsList()
        }
    }
}
