package com.surovtsev.cool3dminesweeper.presentation.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsList
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.SettingsScreenViewModel
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.LightBlue
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.PrimaryColor1
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.MinesweeperTheme
import com.surovtsev.utils.compose.components.CustomSliderWithCaption

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
    val settingsList: SettingsList by viewModel.settingsScreenEvents.settingsListData.run {
        data.observeAsState(defaultValue)
    }
    val selectedSettingsId: Long by viewModel.settingsScreenControls.selectedSettingsId.run {
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
                val modifier = Modifier.clickable { viewModel.selectSettings(item) }.let {
                    if (selectedSettingsId == itemId) {
                        it.background(LightBlue)
                    } else {
                        it
                    }
                }
                Box (
                    modifier
                ) {
                    SettingsDataItem(viewModel = viewModel, item)
                }
            }
        }
    }
}

@Composable
fun SettingsDataItem(
    viewModel: SettingsScreenViewModel,
    settings: Settings
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val settingsData = settings.settingsData
        val counts = settingsData.dimensions.toVec3i()
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
                .clickable { viewModel.deleteSettings(settings.id) }
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
            val sliderValue: Int by sV.run {
                data.observeAsState(defaultValue)
            }
            val borders = bordersAndValue.first
            CustomSliderWithCaption(
                name,
                borders,
                sliderValue,
                sV::onDataChanged,
                LightBlue,
                PrimaryColor1
            )
        }
    }
}


@Composable
fun UseButton(
    viewModel: SettingsScreenViewModel
) {
    Button (
        { viewModel.applySettings() },
        modifier = Modifier
            .fillMaxWidth(fraction = 0.75f)
    ) {
        Text(
            "use",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}
