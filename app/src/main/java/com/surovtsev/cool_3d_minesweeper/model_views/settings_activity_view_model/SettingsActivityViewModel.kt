package com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_view_model

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import javax.inject.Inject

@SettingsScope
class SettingsActivityViewModel @Inject constructor(
    private val settingsDBQueries: SettingsDBQueries,
    private val saveController: SaveController,
    val settingsActivityControls: SettingsActivityControls,
    val settingsActivityEvents: SettingsActivityEvents,
    val settingsDataFactory: () -> SettingsData
) {

    var finishAction: (() -> Unit)? = null

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
