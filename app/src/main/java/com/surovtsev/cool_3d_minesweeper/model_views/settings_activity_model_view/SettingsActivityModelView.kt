package com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_model_view

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.AppScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database.SettingsDataHelper
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject
import kotlin.math.round

@AppScope
class SettingsActivityModelView @Inject constructor(
    private val settingsDBQueries: SettingsDBQueries,
    private val saveController: SaveController
) {

    var finishAction: (() -> Unit)? = null

    companion object {
        private val borders = SettingsDataHelper.borders

        private fun createMyLiveDataForSlider(defValue: Int) = MyLiveData(defValue.toFloat())

        fun floatToInt(x: Float) = round(x).toInt()
    }

    val settingsList = MyLiveData(
        listOf<DataWithId<SettingsData>>()
    )

    private val xCountSliderValue = createMyLiveDataForSlider(
        SettingsData.xCountDefaultValue)
    private val yCountSliderValue = createMyLiveDataForSlider(
        SettingsData.yCountDefaultValue)
    private val zCountSliderValue = createMyLiveDataForSlider(
        SettingsData.zCountDefaultValue)
    private val bombsPercentageSliderValue = createMyLiveDataForSlider(
        SettingsData.bombsPercentageDefaultValue)

    private val sliderValues = mapOf(
        SettingsData.xCountName to xCountSliderValue,
        SettingsData.yCountName to yCountSliderValue,
        SettingsData.zCountName to zCountSliderValue,
        SettingsData.bombsPercentageName to bombsPercentageSliderValue
    )

    val slidersInfo = sliderValues.map { (name, value) ->
        val border = borders[name]!!
        name to (border to value)
    }

    val selectedSettingsId = MyLiveData(-1)

    fun loadData() {
        settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        val loadedSettingsData = saveController.loadSettingDataOrDefault()
        setControlValues(loadedSettingsData)

        settingsDBQueries.getId(
            loadedSettingsData
        )?.let {
            selectedSettingsId.onDataChanged(it)
        }
    }

    fun useSettings() {
        val settingsData = SettingsData(
            xCountSliderValue.getValueOrDefault().toInt(),
            yCountSliderValue.getValueOrDefault().toInt(),
            zCountSliderValue.getValueOrDefault().toInt(),
            bombsPercentageSliderValue.getValueOrDefault().toInt()
        )
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
        xCountSliderValue.onDataChanged(
            settingsData.xCount.toFloat()
        )
        yCountSliderValue.onDataChanged(
            settingsData.yCount.toFloat()
        )
        zCountSliderValue.onDataChanged(
            settingsData.zCount.toFloat()
        )
        bombsPercentageSliderValue.onDataChanged(
            settingsData.bombsPercentage.toFloat()
        )
    }

    fun useSettings(settingsDataWithId: DataWithId<SettingsData>) {
        selectedSettingsId.onDataChanged(settingsDataWithId.id)
        setControlValues(settingsDataWithId.data)
    }

    fun deleteSettings(settingsId: Int) {
        settingsDBQueries.delete(settingsId)
        settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )
    }
}
