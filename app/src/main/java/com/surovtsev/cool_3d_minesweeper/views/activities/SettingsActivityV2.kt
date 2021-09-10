package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surovtsev.cool_3d_minesweeper.utils.live_data.MyLiveData
import com.surovtsev.cool_3d_minesweeper.views.theme.PrimaryColor1
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.*
import com.surovtsev.cool_3d_minesweeper.views.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.views.theme.LightBlue
import kotlin.math.round

class SettingsActivityV2: ComponentActivity() {
    private class ModelView {
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
    }

    private val modelView = ModelView()
    private val dbHelper = DBHelper(this)
    private val settingsDBQueries = SettingsDBQueries(dbHelper)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Test_composeTheme {
                Box(
                    Modifier.background(GrayBackground)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.Black)
                        ) {
                            SettingsList()
                        }
                        Column(
                            modifier = Modifier
                                .border(1.dp, Color.Black)
                        ) {
                            Controls()
                        }
                        Column(
                        ) {
                            UseButton()
                        }
                    }
                }
            }
        }

        val loadedSettingsData =
            ApplicationController.instance.saveController.tryToLoad<SettingsData>(
                SaveTypes.GameSettingsJson
            )?: SettingsData()


        loadedSettingsData.getMap().map { (k, v) ->
            setValue(k, v)
        }

        modelView.settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )
    }

    private fun useSettings() {
        val controlsValues = modelView.controlsValues.data.value!!
        val settingsData = SettingsData(controlsValues)
        settingsDBQueries.insertIfNotPresent(
            settingsData
        )

        ApplicationController.instance.saveController.save(
            SaveTypes.GameSettingsJson,
            settingsData
        )

        finish()
        return
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SettingsList() {
        val settingsList: List<DataWithId<SettingsData>> by modelView.settingsList.data.observeAsState(
            listOf<DataWithId<SettingsData>>()
        )
        val selectedSettingsId: Int by modelView.selectedSettingsId.data.observeAsState(-1)

        Box(
            modifier = Modifier
                .background(GrayBackground)
                .border(1.dp, Color.Black),
        ) {
            Column(
                Modifier.fillMaxSize()
            ) {
                Row() {
                    Text(
                        "counts",
                        Modifier.fillMaxWidth(0.33f),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        "bombs %",
                        Modifier.fillMaxWidth(0.5f),
                        textAlign = TextAlign.Center
                    )
                }
                LazyColumn {
                    items(settingsList) { item ->
                        val itemId = item.id
                        if (selectedSettingsId == itemId) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        LightBlue
                                    )

                            ) {
                                SettingsDataItem(item)
                            }
                        } else {
                            Surface (
                                shape = MaterialTheme.shapes.large,
                                onClick = { useSettings(item) },
                            ) {
                                SettingsDataItem(item)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun useSettings(settingsDataWithId: DataWithId<SettingsData>) {
        modelView.selectedSettingsId.onDataChanged(settingsDataWithId.id)
        modelView.controlsValues.onDataChanged(
            settingsDataWithId.data.getMap()
        )
    }

    private fun deleteSettings(settingsId: Int) {
        settingsDBQueries.delete(settingsId)
        modelView.settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )
    }

    private fun setValue(name: String, value: Int) {
        val controlsValues = modelView.controlsValues.data.value!!.toMutableMap()
        controlsValues[name] = value
        modelView.controlsValues.onDataChanged(
            controlsValues
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SettingsDataItem(settingDataWithId: DataWithId<SettingsData>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val settingsData = settingDataWithId.data
            val counts = settingsData.getCounts()
            Text(
                counts.toString(),
                Modifier.fillMaxWidth(0.33f),
                textAlign = TextAlign.Start
            )
            Text(
                settingsData.bombsPercentage.toString(),
                Modifier.fillMaxWidth(0.5f),
                textAlign = TextAlign.Center
            )
            Surface (
                shape = MaterialTheme.shapes.large,
                onClick = { deleteSettings(settingDataWithId.id) },
                color = PrimaryColor1
            ) {
                Text(
                    "delete",
                    textAlign = TextAlign.End
                )
            }
        }
    }

    private fun toFloatRange(x: IntRange): ClosedFloatingPointRange<Float> =
        x.first.toFloat()..x.last.toFloat()

    @Composable
    fun Controls() {
        val controlsValues: Map<String, Int> by modelView.controlsValues.data.observeAsState(
            ModelView.paramNames.map { it to ModelView.borders[it]!!.first }.toMap()
        )

        LazyColumn {
            items(ModelView.paramNames) { item ->
                val intRange = ModelView.borders[item]!!
                val valueRange = toFloatRange(
                    intRange
                )
                val steps = intRange.last - intRange.first - 1
                val value = controlsValues[item]!!
                Column() {
                    Row() {
                        Text(
                            intRange.start.toString(),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(0.33f)
                        )
                        Text(
                            value.toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                        Text(
                            intRange.last.toString(),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Slider(
                        value = value.toFloat(),
                        onValueChange = { setValue(item, round(it).toInt()) },
                        valueRange = valueRange,
                        steps = steps,
                        enabled = true
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun UseButton() {
        Surface (
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black),
            color = PrimaryColor1,
            shape = MaterialTheme.shapes.large,
            onClick = { useSettings() },
        ) {
            Text(
                "Use",
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )
        }
    }
}
