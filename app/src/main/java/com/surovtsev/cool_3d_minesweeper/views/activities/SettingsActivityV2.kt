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
import com.surovtsev.cool_3d_minesweeper.views.theme.PrimaryColor1
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.*
import com.surovtsev.cool_3d_minesweeper.model_views.SettingActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.views.theme.LightBlue
import kotlin.math.round

class SettingsActivityV2: ComponentActivity() {

    private val modelView = SettingActivityModelView(this::finish)

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
                            SettingsList(modelView)
                        }
                        Column(
                            modifier = Modifier
                                .border(1.dp, Color.Black)
                        ) {
                            Controls(modelView)
                        }
                        Column(
                        ) {
                            UseButton(modelView)
                        }
                    }
                }
            }
        }

        modelView.loadData()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsList(modelView: SettingActivityModelView) {
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
                            SettingsDataItem(modelView,item)
                        }
                    } else {
                        Surface (
                            shape = MaterialTheme.shapes.large,
                            onClick = { modelView.useSettings(item) },
                        ) {
                            SettingsDataItem(modelView, item)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsDataItem(
    modelView: SettingActivityModelView,
    settingDataWithId: DataWithId<SettingsData>) {
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
            onClick = { modelView.deleteSettings(settingDataWithId.id) },
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
fun Controls(
    modelView: SettingActivityModelView
) {
    val paramNames = SettingActivityModelView.paramNames
    val sliderValues = modelView.sliderValues
    val borders = SettingActivityModelView.borders

    LazyColumn {
        items(paramNames) { param ->
            val sV = sliderValues[param]!!
            val sliderValue: Float by sV.data.observeAsState(
                sV.defaultValue
            )
            val border = borders[param]!!
            MySlider(intRange = border, value = sliderValue, onChangeAction = sV::onDataChanged)
        }
    }
}

@Composable
fun MySlider(
    intRange: IntRange,
    value: Float,
    onChangeAction: (Float) -> Unit,
) {
    val valueRange = toFloatRange(intRange)
    val steps = intRange.last - intRange.first - 1
    Column() {
        Row() {
            Text(
                intRange.start.toString(),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.33f)
            )
            Text(
                SettingActivityModelView.floatToInt(value).toString(),
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
            value = value,
            onValueChange = onChangeAction,
            valueRange = valueRange,
            steps = steps,
            enabled = true
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UseButton(
    modelView: SettingActivityModelView
) {
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black),
        color = PrimaryColor1,
        shape = MaterialTheme.shapes.large,
        onClick = { modelView.useSettings() },
    ) {
        Text(
            "Use",
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

