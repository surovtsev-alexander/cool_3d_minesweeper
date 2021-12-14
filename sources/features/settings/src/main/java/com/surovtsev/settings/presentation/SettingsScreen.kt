package com.surovtsev.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.ui.theme.GrayBackground
import com.surovtsev.core.ui.theme.LightBlue
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.core.ui.theme.PrimaryColor1
import com.surovtsev.settings.viewmodel.*
import com.surovtsev.settings.viewmodel.helpers.SettingsUIInfo
import com.surovtsev.utils.compose.components.CustomSliderWithCaption

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavController
) {
    val settingsScreenCommandsHandler: SettingsScreenCommandsHandler = viewModel
    LaunchedEffect(key1 = Unit) {
        viewModel.finishAction = { navController.navigateUp() }
        settingsScreenCommandsHandler.handleCommand(
            CommandFromSettingsScreen.LoadSettings
        )
    }

    SettingsControls(
        viewModel.settingsScreenStateValue,
        settingsScreenCommandsHandler
    )
}

@Composable
fun SettingsControls(
    settingsScreenStateValue: SettingsScreenStateValue,
    settingsScreenCommandsHandler: SettingsScreenCommandsHandler,
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
                    SettingsList(
                        settingsScreenStateValue,
                        settingsScreenCommandsHandler
                    )
                }
                Column(
                    modifier = Modifier
                        .border(1.dp, Color.Black)
                ) {
                    Controls(
                        settingsScreenStateValue,
                        settingsScreenCommandsHandler
                    )
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
                    ApplyButton(
                        settingsScreenCommandsHandler
                    )
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
fun SettingsList(
    settingsScreenStateValue: SettingsScreenStateValue,
    settingsScreenCommandsHandler: SettingsScreenCommandsHandler
) {
    val state = settingsScreenStateValue.observeAsState(
        SettingsScreenInitialState
    ).value

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
            val screenData = state.screenData
            val settingsList = if (screenData is SettingsScreenData.SettingsLoaded) {
                screenData.settingsList
            } else {
                emptyList()
            }

            items(settingsList) { item: Settings ->
                val itemId = item.id
                val modifier = Modifier.clickable {
                    settingsScreenCommandsHandler.handleCommand(
                        CommandFromSettingsScreen.RememberSettings(
                            item
                        )
                    )
                }.let {
                    if (screenData is SettingsScreenData.SettingsIsSelected &&
                        screenData.settingsId == itemId
                    ) {
                        it.background(LightBlue)
                    } else {
                        it
                    }
                }
                Box (
                    modifier
                ) {
                    SettingsDataItem(settingsScreenCommandsHandler, item)
                }
            }
        }
    }
}

@Composable
fun SettingsDataItem(
    settingsScreenCommandsHandler: SettingsScreenCommandsHandler,
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
                .clickable {
                    settingsScreenCommandsHandler.handleCommand(
                        CommandFromSettingsScreen.DeleteSettings(settings.id)
                    )
                }
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
    settingsScreenStateValue: SettingsScreenStateValue,
    settingsScreenCommandsHandler: SettingsScreenCommandsHandler
) {
    val state = settingsScreenStateValue.observeAsState(
        SettingsScreenInitialState
    ).value
    val screenData = state.screenData

    if (screenData !is SettingsScreenData.SettingsDataIsSelected) {
        return
    }

    val settingsData = screenData.settingsData

    val rememberSettingsAction = { updatedSettingsData: Settings.SettingsData ->
        settingsScreenCommandsHandler.handleCommand(
            CommandFromSettingsScreen.RememberSettingsData(
                updatedSettingsData
            )
        )
    }

    LazyColumn {
        items(SettingsUIInfo.info) { item ->
            CustomSliderWithCaption(
                name = item.title,
                borders = item.borders,
                sliderPosition = item.valueCalculator(settingsData),
                onChangeAction = {
                    val updatedSettingsData = item.settingsDataCalculator(settingsData, it)
                    rememberSettingsAction(updatedSettingsData)
                },
                backgroundColor = LightBlue,
                lineColor = PrimaryColor1,
            )
        }

    }
     /*
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
         */
}


@Composable
fun ApplyButton(
    screenStateCommandsHandler: SettingsScreenCommandsHandler
) {
    Button (
        {
            screenStateCommandsHandler.handleCommand(
                CommandFromSettingsScreen.ApplySettings
            )
        },
        modifier = Modifier
            .fillMaxWidth(fraction = 0.75f)
    ) {
        Text(
            "apply",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}
