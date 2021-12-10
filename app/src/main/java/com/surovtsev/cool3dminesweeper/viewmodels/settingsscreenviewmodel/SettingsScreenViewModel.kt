package com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveTypes
import com.surovtsev.cool3dminesweeper.dagger.app.settings.SettingsComponent
import com.surovtsev.cool3dminesweeper.dagger.app.settings.SettingsComponentEntryPoint
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings
import com.surovtsev.cool3dminesweeper.models.room.entities.SettingsDataFactory
import com.surovtsev.utils.viewmodel.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.viewmodel.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers.SettingsScreenControls
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers.SettingsScreenEvents
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers.SlidersWithNames
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    settingsComponentProvider: Provider<SettingsComponent.Builder>,
):
    ViewModel(),
    DefaultLifecycleObserver,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{

    private val slidersWithNames: SlidersWithNames
    val settingsScreenControls: SettingsScreenControls
    private val settingsDao: SettingsDao
    private val saveController: SaveController
    val settingsScreenEvents: SettingsScreenEvents
    val settingsDataFactory: SettingsDataFactory

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
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        loadData()
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
