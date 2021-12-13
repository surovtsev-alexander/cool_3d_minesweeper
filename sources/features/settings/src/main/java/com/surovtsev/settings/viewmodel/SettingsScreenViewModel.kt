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

        loadData()
    }

    override fun handleCommand(event: CommandFromSettingsScreen) {
        TODO("Not yet implemented")
    }

    private suspend fun reloadSettingsList() {
        val settingsList = settingsDao.getAll()

        withUIContext {
            settingsScreenEvents.settingsListData.onDataChanged(
                settingsList
            )
        }
    }

    private fun loadData() {
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

    fun applySettings() {
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

    fun selectSettings(settings: Settings) {
        settingsScreenControls.selectedSettingsId.onDataChanged(settings.id)
        setControlValues(settings.settingsData)
    }

    fun deleteSettings(settingsId: Long) {
        launchOnIOThread {
            settingsDao.delete(settingsId)

            reloadSettingsList()
        }
    }
}
