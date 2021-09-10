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
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDataHelper
import com.surovtsev.cool_3d_minesweeper.utils.live_data.MyLiveData
import com.surovtsev.cool_3d_minesweeper.views.theme.PrimaryColor1
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData
import kotlin.math.round

class SettingsActivityV2: ComponentActivity() {

    private val settingsDBQueries: SettingsDBQueries = SettingsDBQueries(DBHelper(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Test_composeTheme {
                Box(
                    Modifier.background(Color(0xFF48cae4))
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
    }
    
    @Composable
    fun SettingsList() {
        
    }

    private class ModelView() {
        companion object {
            val paramNames = SettingsDataHelper.paramNames
            val borders = SettingsDataHelper.borders
        }

        val controlsValues = MyLiveData<Map<String, Int>>(
            paramNames.map { it to borders[it]!!.first }.toMap()
        )
    }

    private val modelView = ModelView()

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

    private fun setValue(name: String, value: Int) {
        val controlsValues = modelView.controlsValues.data.value!!.toMutableMap()
        controlsValues[name] = value
        modelView.controlsValues.onDataChanged(
            controlsValues
        )
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

    fun useSettings() {
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
}
