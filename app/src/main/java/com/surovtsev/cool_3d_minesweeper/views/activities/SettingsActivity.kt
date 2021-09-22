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
import com.surovtsev.cool_3d_minesweeper.model_views.SettingsActivityModelView
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.views.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.views.theme.LightBlue

class SettingsActivity: ComponentActivity() {

    private val modelView = SettingsActivityModelView(this::finish)

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
                        Column {
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
fun SettingsList(modelView: SettingsActivityModelView) {
    val settingsList: List<DataWithId<SettingsData>> by modelView.settingsList.run {
        data.observeAsState(defaultValue)
    }
    val selectedSettingsId: Int by modelView.selectedSettingsId.run {
        data.observeAsState(defaultValue)
    }

    Box(
        modifier = Modifier
            .background(GrayBackground)
            .border(1.dp, Color.Black),
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Row {
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
    modelView: SettingsActivityModelView,
    settingDataWithId: DataWithId<SettingsData>
) {
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
    modelView: SettingsActivityModelView
) {
    val slidersInfo = modelView.slidersInfo

    LazyColumn {
        items(slidersInfo) { (name, bordersAndValue) ->
            val sV = bordersAndValue.second
            val sliderValue: Float by sV.run {
                data.observeAsState(defaultValue)
            }
            val borders = bordersAndValue.first
            MySlider(
                name,
                borders,
                sliderValue,
                sV::onDataChanged
            )
        }
    }
}

@Composable
fun MySlider(
    name: String,
    borders: IntRange,
    value: Float,
    onChangeAction: (Float) -> Unit,
) {
    val valueRange = toFloatRange(borders)
    val steps = borders.last - borders.first - 1
    Column {
        Row {
            Text(
                name,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.33f)
            )
            Text(
                SettingsActivityModelView.floatToInt(value).toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            Text(
                "(${borders})",
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
    modelView: SettingsActivityModelView
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

