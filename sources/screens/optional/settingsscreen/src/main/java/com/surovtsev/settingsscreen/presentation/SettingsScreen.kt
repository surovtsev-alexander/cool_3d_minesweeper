package com.surovtsev.settingsscreen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.surovtsev.core.viewmodel.PlaceErrorDialog
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.EventToSettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.SettingsScreenData
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenErrorDialogPlacer
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenEventReceiver
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenStateFlow
import com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo.SettingUIControl
import com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo.SettingsUIControlsInfo
import com.surovtsev.utils.compose.components.CustomSliderWithCaption

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavController
) {
    val eventReceiver = viewModel.eventReceiver
    LaunchedEffect(key1 = Unit) {
        viewModel.finishActionHolder.finishAction = {
            navController.navigateUp()
        }
        eventReceiver.pushEventAsync(
            EventToSettingsScreenViewModel.TriggerInitialization
        )
    }

    SettingsControls(
        viewModel.screenStateFlow,
        eventReceiver,
        viewModel as SettingsScreenErrorDialogPlacer,
    )
}

@Composable
fun SettingsControls(
    stateFlow: SettingsScreenStateFlow,
    eventReceiver: SettingsScreenEventReceiver,
    errorDialogPlacer: SettingsScreenErrorDialogPlacer,
) {
    MinesweeperTheme {

        errorDialogPlacer.PlaceErrorDialog()
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
                        stateFlow,
                        eventReceiver
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
                        stateFlow,
                        eventReceiver
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
                    ApplySettingsButtons(
                        eventReceiver
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsList(
    stateFlow: SettingsScreenStateFlow,
    eventReceiver: SettingsScreenEventReceiver
) {
    val state = stateFlow.collectAsState().value

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
            val screenData = state.data
            val settingsList = if (screenData is SettingsScreenData.SettingsLoaded) {
                screenData.settingsList
            } else {
                emptyList()
            }

            items(settingsList) { item: Settings ->
                val itemId = item.id
                val modifier = Modifier.clickable {
                    eventReceiver.pushEventAsync(
                        EventToSettingsScreenViewModel.RememberSettings(
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
                    SettingsDataItem(eventReceiver, item)
                }
            }
        }
    }
}

@Composable
fun SettingsDataItem(
    eventReceiver: SettingsScreenEventReceiver,
    settings: Settings
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(3.dp)
    ) {
        val settingsData = settings.settingsData
        val counts = settingsData.dimensions.toVec3i()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(4f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                counts.toString(),
                Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(4f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                settingsData.bombsPercentage.toString(),
                Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f),
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        eventReceiver.pushEventAsync(
                            EventToSettingsScreenViewModel.DeleteSettings(settings.id)
                        )
                    }
                    .border(1.dp, Color.Black)
                    .background(PrimaryColor1),
            ) {
                Text(
                    "delete",
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun Controls(
    stateFlow: SettingsScreenStateFlow,
    eventReceiver: SettingsScreenEventReceiver
) {
    val state = stateFlow.collectAsState().value
    val screenData = state.data

    if (screenData !is SettingsScreenData.SettingsDataIsSelected) {
        return
    }

    val settingsUIInfo by remember { mutableStateOf(SettingsUIControlsInfo()) }

    settingsUIInfo.info.map { settingUIControl ->
        BindViewModelAndUI(
            screenData = screenData,
            eventReceiver = eventReceiver,
            settingsUIControl = settingUIControl
        )
    }

    LazyColumn {
        items(settingsUIInfo.info) { settingUIControl ->
            CustomSliderWithCaption(
                name = settingUIControl.title,
                borders = settingUIControl.borders,
                sliderPositionMutableStateFlow = settingUIControl.sliderPositionMutableStateFlow,
                backgroundColor = LightBlue,
                lineColor = PrimaryColor1,
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BindViewModelAndUI(
    screenData: SettingsScreenData.SettingsDataIsSelected,
    eventReceiver: SettingsScreenEventReceiver,
    settingsUIControl: SettingUIControl
) {
    val settingsData = screenData.settingsData

    val uiValue = settingsUIControl.sliderPositionMutableStateFlow.collectAsState().value
    val viewModelValue = settingsUIControl.valueCalculator(settingsData)

    var prevUIValue by remember { mutableStateOf(-1) }
    var prevViewModelValue by remember { mutableStateOf(-1) }

    val uiAndViewModelValuesSame = uiValue == viewModelValue

    val uiUpdated = uiValue != prevUIValue
    val viewModelValueUpdated = viewModelValue != prevViewModelValue

    val fromUI = screenData.fromSlider

    if (viewModelValueUpdated) {
        prevViewModelValue = viewModelValue
        if (!fromUI && !uiAndViewModelValuesSame) {
            settingsUIControl.sliderPositionMutableStateFlow.value = viewModelValue
            prevUIValue = viewModelValue
        }
    } else if (uiUpdated) {
        prevUIValue = uiValue

        val rememberSettingsAction = { updatedSettingsData: Settings.SettingsData ->
            eventReceiver.pushEventAsync(
                EventToSettingsScreenViewModel.RememberSettingsData(
                    updatedSettingsData, fromSlider = true
                )
            )
        }

        rememberSettingsAction(
            settingsUIControl.settingsDataCalculator(settingsData, uiValue)
        )
    }
}

@Composable
fun ApplySettingsButtons(
    eventReceiver: SettingsScreenEventReceiver
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.75f),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val buttons = arrayOf(
            "ok" to EventToSettingsScreenViewModel.ApplySettings,
            "back" to EventToSettingsScreenViewModel.Finish
        )

        buttons.map { (buttonCaption, event) ->
            Button(
                {
                    eventReceiver.pushEventAsync(
                        event
                    )
                },
                modifier = Modifier
                    .weight(1f),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(
                    buttonCaption,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
