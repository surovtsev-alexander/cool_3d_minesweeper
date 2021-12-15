package com.surovtsev.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.surovtsev.settings.viewmodel.helpers.SettingUIControl
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp, 15.dp),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    SettingsList(
                        settingsScreenStateValue,
                        settingsScreenCommandsHandler
                    )
                }

                Divider(
                    modifier = Modifier
                        .padding(10.dp),
                    color = Color.Black,
                    thickness = 1.dp
                )

                Column {
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
            .background(GrayBackground),
    ) {
        Row {
            Text(
                "counts",
                Modifier.weight(4f),
                textAlign = TextAlign.Start
            )
            Text(
                "bombs %",
                Modifier.weight(4f),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .weight(2f))
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
            .padding(0.dp, 2.dp)
    ) {
        val settingsData = settings.settingsData
        val counts = settingsData.dimensions.toVec3i()
        Text(
            counts.toString(),
            Modifier.weight(4f),
            textAlign = TextAlign.Start
        )
        Text(
            settingsData.bombsPercentage.toString(),
            Modifier.weight(4f),
            textAlign = TextAlign.Center
        )
        Box (
            modifier = Modifier
                .weight(2f)
                .clickable {
                    settingsScreenCommandsHandler.handleCommand(
                        CommandFromSettingsScreen.DeleteSettings(settings.id)
                    )
                }
                .background(PrimaryColor1)
        ) {
            Text(
                "delete",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

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

    val settingsUIInfo by remember { mutableStateOf(SettingsUIInfo()) }

    settingsUIInfo.info.map { settingUIControl ->
        BindViewModelAndUI(
            screenData = screenData,
            settingsScreenCommandsHandler = settingsScreenCommandsHandler,
            settingsUIControl = settingUIControl
        )
    }

    LazyColumn {
        items(settingsUIInfo.info) { item ->
            CustomSliderWithCaption(
                name = item.title,
                borders = item.borders,
                sliderPositionData = item.sliderPositionData,
                backgroundColor = LightBlue,
                lineColor = PrimaryColor1,
            )
        }
    }
}

@Composable
fun BindViewModelAndUI(
    screenData: SettingsScreenData.SettingsDataIsSelected,
    settingsScreenCommandsHandler: SettingsScreenCommandsHandler,
    settingsUIControl: SettingUIControl
) {
    val settingsData = screenData.settingsData

    val uiValue = settingsUIControl.sliderPositionData.observeAsState().value!!
    val viewModelValue = settingsUIControl.valueCalculator(settingsData)

    var prevUIValue by remember { mutableStateOf(-1) }
    var prevViewModelValue by remember { mutableStateOf(-1) }

    val uiAndViewModelValuesSame = uiValue == viewModelValue

    val uiUpdated = uiValue != prevUIValue
    val viewModelValueUpdated = viewModelValue != prevViewModelValue

    val fromUI = screenData.fromUI

    if (viewModelValueUpdated) {
        prevViewModelValue = viewModelValue
        if (!fromUI && !uiAndViewModelValuesSame) {
            settingsUIControl.sliderPositionData.value = viewModelValue
            prevUIValue = viewModelValue
        }
    } else if (uiUpdated) {
        prevUIValue = uiValue

        val rememberSettingsAction = { updatedSettingsData: Settings.SettingsData ->
            settingsScreenCommandsHandler.handleCommand(
                CommandFromSettingsScreen.RememberSettingsData(
                    updatedSettingsData, fromUI = true
                )
            )
        }

        rememberSettingsAction(
            settingsUIControl.settingsDataCalculator(settingsData, uiValue)
        )
    }
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
