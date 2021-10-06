package com.surovtsev.cool_3d_minesweeper.model_views.settings_screen_view_model

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
import com.surovtsev.cool_3d_minesweeper.model_views.settings_screen_view_model.helpers.SettingsScreenControls
import com.surovtsev.cool_3d_minesweeper.model_views.settings_screen_view_model.helpers.SettingsScreenEvents
import com.surovtsev.cool_3d_minesweeper.model_views.settings_screen_view_model.helpers.SlidersWithNames
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsDataFactory
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    settingsComponentProvider: Provider<SettingsComponent.Builder>,
): ViewModel(), LifecycleObserver {

    private val slidersWithNames: SlidersWithNames
    val settingsScreenControls: SettingsScreenControls
    private val settingsDBQueries: SettingsDBQueries
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
        settingsDBQueries =
            settingsComponentEntryPoint.settingsDBQueries
        saveController =
            settingsComponentEntryPoint.saveController
        settingsScreenEvents =
            settingsComponentEntryPoint.settingsScreenEvents
        settingsDataFactory =
            settingsComponentEntryPoint.settingsDataFactory
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d("TEST+++", "SettingsActivityViewModel onCreate")
        loadData()
    }

    private fun loadData() {
        settingsScreenEvents.settingsListWithIds.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        val loadedSettingsData = saveController.loadSettingDataOrDefault()
        setControlValues(loadedSettingsData)

        settingsDBQueries.getId(
            loadedSettingsData
        )?.let {
            settingsScreenControls.selectedSettingsId.onDataChanged(it)
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
        settingsScreenControls.slidersWithNames.let {
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
        settingsScreenControls.selectedSettingsId.onDataChanged(settingsDataWithId.id)
        setControlValues(settingsDataWithId.data)
    }

    fun deleteSettings(settingsId: Int) {
        settingsDBQueries.delete(settingsId)
        settingsScreenEvents.settingsListWithIds.onDataChanged(
            settingsDBQueries.getSettingsList()
        )
    }
}
