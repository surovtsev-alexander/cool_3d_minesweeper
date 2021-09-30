package com.surovtsev.cool_3d_minesweeper.presentation.settings_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.surovtsev.cool_3d_minesweeper.dagger.app.settings.SettingsComponent
import com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder.DaggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_view_model.SettingsActivityViewModel
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.LightBlue
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.PrimaryColor1
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.Test_composeTheme
import kotlin.math.round

@Composable
fun SettingsScreen(
    settingsComponent: SettingsComponent,
    navController: NavController
) {
    val viewModel = settingsComponent.settingsActivityViewModel
    viewModel.loadData()

    viewModel.finishAction = { navController.navigateUp() }
    SettingsControls(viewModel = viewModel)
}

@Composable
fun SettingsControls(
    viewModel: SettingsActivityViewModel
) {
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
                    SettingsList(viewModel)
                }
                Column(
                    modifier = Modifier
                        .border(1.dp, Color.Black)
                ) {
                    Controls(viewModel)
                }
                Column {
                    UseButton(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsList(viewModel: SettingsActivityViewModel) {
    val settingsList: List<DataWithId<SettingsData>> by viewModel.settingsActivityEvents.settingsList.run {
        data.observeAsState(defaultValue)
    }
    val selectedSettingsId: Int by viewModel.settingsActivityControls.selectedSettingsId.run {
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
                            SettingsDataItem(viewModel,item)
                        }
                    } else {
                        Surface (
                            shape = MaterialTheme.shapes.large,
                            onClick = { viewModel.useSettings(item) },
                        ) {
                            SettingsDataItem(viewModel, item)
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
    viewModel: SettingsActivityViewModel,
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
            onClick = { viewModel.deleteSettings(settingDataWithId.id) },
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
    viewModel: SettingsActivityViewModel
) {
    val slidersInfo = viewModel.settingsActivityControls.slidersInfo

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

fun floatToInt(x: Float) = round(x).toInt()

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
                floatToInt(value).toString(),
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
    viewModel: SettingsActivityViewModel
) {
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black),
        color = PrimaryColor1,
        shape = MaterialTheme.shapes.large,
        onClick = { viewModel.useSettings() },
    ) {
        Text(
            "Use",
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}
