package com.surovtsev.cool_3d_minesweeper.model_views

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDataHelper
import com.surovtsev.cool_3d_minesweeper.utils.live_data.MyLiveData

class SettingActivityModelView(
    private val finishAction: () -> Unit
) {
    val applicationController = ApplicationController.getInstance()
    val settingsDBQueries = applicationController.settingsDBQueries
    val saveController = applicationController.saveController

    companion object {
        val paramNames = SettingsDataHelper.paramNames
        val borders = SettingsDataHelper.borders
    }

    val settingsList = MyLiveData<List<DataWithId<SettingsData>>>(
        listOf<DataWithId<SettingsData>>()
    )

    val controlsValues = MyLiveData<Map<String, Int>>(
        paramNames.map { it to borders[it]!!.first }.toMap()
    )

    val selectedSettingsId = MyLiveData<Int>(-1)

    fun loadData() {
        val loadedSettingsData =
            saveController.tryToLoad<SettingsData>(
                SaveTypes.GameSettingsJson
            )?: SettingsData()


        loadedSettingsData.getMap().map { (k, v) ->
            setValue(k, v)
        }

        settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )
    }

    fun useSettings() {
        val controlsValues = controlsValues.data.value!!
        val settingsData = SettingsData(controlsValues)
        settingsDBQueries.insertIfNotPresent(
            settingsData
        )

        saveController.save(
            SaveTypes.GameSettingsJson,
            settingsData
        )

        finishAction()
    }

    fun useSettings(settingsDataWithId: DataWithId<SettingsData>) {
        selectedSettingsId.onDataChanged(settingsDataWithId.id)
        controlsValues.onDataChanged(
            settingsDataWithId.data.getMap()
        )
    }

    fun deleteSettings(settingsId: Int) {
        settingsDBQueries.delete(settingsId)
        settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )
    }

    fun setValue(name: String, value: Int) {
        controlsValues.data.value?.toMutableMap()?.let {
            it[name] = value
            controlsValues.onDataChanged(it)
        }
    }
}
