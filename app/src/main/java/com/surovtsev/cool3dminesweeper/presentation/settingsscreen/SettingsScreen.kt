package com.surovtsev.cool3dminesweeper.presentation.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.SettingsScreenViewModel
import com.surovtsev.cool3dminesweeper.models.game.database.DataWithId
import com.surovtsev.cool3dminesweeper.models.game.database.SettingsData
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.LightBlue
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.PrimaryColor1
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.MinesweeperTheme
import kotlin.math.round

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavController
) {
    viewModel.finishAction = { navController.navigateUp() }
    SettingsControls(viewModel = viewModel)
}

@Composable
fun SettingsControls(
    viewModel: SettingsScreenViewModel
) {
    MinesweeperTheme {
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
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UseButton(viewModel)
                }
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsList(viewModel: SettingsScreenViewModel) {
    val settingsList: List<DataWithId<SettingsData>> by viewModel.settingsScreenEvents.settingsListWithIds.run {
        data.observeAsState(defaultValue)
    }
    val selectedSettingsId: Int by viewModel.settingsScreenControls.selectedSettingsId.run {
        data.observeAsState(defaultValue)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .border(1.dp, Color.Black),
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
                val modifier = Modifier.clickable { viewModel.useSettings(item) }.let {
                    if (selectedSettingsId == itemId) {
                        it.background(LightBlue)
                    } else {
                        it
                    }
                }
                Box (
                    modifier
                ) {
                    SettingsDataItem(viewModel = viewModel, settingDataWithId = item)
                }
            }
        }
    }
}

@Composable
fun SettingsDataItem(
    viewModel: SettingsScreenViewModel,
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
        Box (
            modifier = Modifier
                .clickable { viewModel.deleteSettings(settingDataWithId.id) }
                .background(PrimaryColor1)
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
    viewModel: SettingsScreenViewModel
) {
    val slidersInfo = viewModel.settingsScreenControls.slidersInfo

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

@Composable
fun UseButton(
    viewModel: SettingsScreenViewModel
) {
    Button (
        { viewModel.useSettings() },
        modifier = Modifier
            .fillMaxWidth(fraction = 0.75f)
    ) {
        Text(
            "Use",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}
