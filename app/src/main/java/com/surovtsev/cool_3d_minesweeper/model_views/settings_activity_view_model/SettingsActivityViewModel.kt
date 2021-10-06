package com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_view_model

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.settings.SettingsComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.settings.SettingsComponentEntryPoint
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsDataFactory
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class SettingsActivityViewModel @Inject constructor(
    settingsComponentProvider: Provider<SettingsComponent.Builder>,
): ViewModel(), LifecycleObserver {

    private val slidersWithNames: SlidersWithNames
    val settingsActivityControls: SettingsActivityControls
    private val settingsDBQueries: SettingsDBQueries
    private val saveController: SaveController
    val settingsActivityEvents: SettingsActivityEvents
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
        settingsActivityControls =
            settingsComponentEntryPoint.settingsActivityControls
        settingsDBQueries =
            settingsComponentEntryPoint.settingsDBQueries
        saveController =
            settingsComponentEntryPoint.saveController
        settingsActivityEvents =
            settingsComponentEntryPoint.settingsActivityEvents
        settingsDataFactory =
            settingsComponentEntryPoint.settingsDataFactory
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d("TEST+++", "SettingsActivityViewModel onCreate")
        loadData()
    }

    fun loadData() {
        settingsActivityEvents.settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        val loadedSettingsData = saveController.loadSettingDataOrDefault()
        setControlValues(loadedSettingsData)

        settingsDBQueries.getId(
            loadedSettingsData
        )?.let {
            settingsActivityControls.selectedSettingsId.onDataChanged(it)
        }
    }

    fun useSettings() {
        val settingsData = settingsDataFactory()
        settingsDBQueries.insertIfNotPresent(
            settingsData
        )

        saveController.save(
            SaveTypes.GameSettingsJson,
            settingsData
        )

        finishAction?.invoke()
    }

    private fun setControlValues(settingsData: SettingsData) {
        settingsActivityControls.slidersWithNames.let {
            it[SettingsData.xCountName]!!.onDataChanged(
                settingsData.xCount.toFloat())
            it[SettingsData.yCountName]!!.onDataChanged(
                settingsData.yCount.toFloat())
            it[SettingsData.zCountName]!!.onDataChanged(
                settingsData.zCount.toFloat())
            it[SettingsData.bombsPercentageName]!!.onDataChanged(
                settingsData.bombsPercentage.toFloat())

        }
    }

    fun useSettings(settingsDataWithId: DataWithId<SettingsData>) {
        settingsActivityControls.selectedSettingsId.onDataChanged(settingsDataWithId.id)
        setControlValues(settingsDataWithId.data)
    }

    fun deleteSettings(settingsId: Int) {
        settingsDBQueries.delete(settingsId)
        settingsActivityEvents.settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )
    }
}
