/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.settingsscreen.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.surovtsev.finitestatemachine.eventreceiver.EventReceiver
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.EventToSettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.SettingsScreenData
import com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo.UIControlsInfo
import com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo.slidersinfo.SlidersInfo
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.templateviewmodel.helpers.errordialog.ErrorDialogPlacer
import com.surovtsev.templateviewmodel.helpers.errordialog.PlaceErrorDialog
import com.surovtsev.templateviewmodel.helpers.errordialog.ScreenStateFlow
import com.surovtsev.utils.compose.components.intslider.IntSlider
import com.surovtsev.utils.compose.components.scrollbar.LazyListScrollbarContext
import com.surovtsev.utils.compose.components.scrollbar.ScrollBar

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavController,
    dipCoefficient: Float,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.finishActionHolder.finishAction = {
            navController.navigateUp()
        }
        viewModel.restartFSM()
    }

    SettingsControls(
        viewModel.screenStateFlow,
        viewModel.uiControlsInfo,
        viewModel.finiteStateMachine.eventReceiver,
        viewModel as ErrorDialogPlacer,
        dipCoefficient,
    )
}

@Composable
fun SettingsControls(
    screenStateFlow: ScreenStateFlow,
    uiControlsInfo: UIControlsInfo,
    eventReceiver: EventReceiver,
    errorDialogPlacer: ErrorDialogPlacer,
    dipCoefficient: Float,
) {
    MinesweeperTheme {

        errorDialogPlacer.PlaceErrorDialog(GrayBackground)

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
                        screenStateFlow,
                        uiControlsInfo,
                        eventReceiver,
                        dipCoefficient,
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
                        screenStateFlow,
                        uiControlsInfo.slidersInfo,
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
    screenStateFlow: ScreenStateFlow,
    uiControlsInfo: UIControlsInfo,
    eventReceiver: EventReceiver,
    dipCoefficient: Float,
) {
    val screenState = screenStateFlow.collectAsState().value
    val screenData = screenState.data

    val selectedSettingsId = uiControlsInfo.selectedSettingsId.collectAsState().value

    val settingsList = if (screenData is SettingsScreenData.Initialized) {
        screenData.settingsList
    } else {
        emptyList()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(GrayBackground),
    ) {
        val lazyListScrollbarContext = LazyListScrollbarContext(
            rememberLazyListState(),
            dipCoefficient,
        ).apply {
            updateElementsCount(settingsList.count())
        }
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
                    .weight(2f)
            )
            Spacer(modifier = Modifier.width(lazyListScrollbarContext.widthDp))
        }
        Row {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = lazyListScrollbarContext.lazyListState
            ) {
                items(settingsList) { item: Settings ->
                    val itemId = item.id
                    val modifier = Modifier.clickable {
                        uiControlsInfo.slidersInfo.updateInfo(item.settingsData)
                    }.let {
                        if (itemId == selectedSettingsId) {
                            it.background(LightBlue)
                        } else {
                            it
                        }
                    }
                    Box(
                        modifier
                    ) {
                        SettingsDataItem(eventReceiver, item)
                    }
                }
            }
            ScrollBar(
                modifier = Modifier,
                lazyListScrollableContext = lazyListScrollbarContext
            )
        }
    }
}

@Composable
fun SettingsDataItem(
    eventReceiver: EventReceiver,
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
                        eventReceiver.receiveEvent(
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
    screenStateFlow: ScreenStateFlow,
    slidersInfo: SlidersInfo,
    eventReceiver: EventReceiver
) {
    val screenState = screenStateFlow.collectAsState().value
    val screenData = screenState.data

    if (screenData !is SettingsScreenData.Initialized) {
        return
    }

    LazyColumn {
        items(slidersInfo.info) { control ->
            val currentPosition = control.position.collectAsState().value
            IntSlider(
                position = currentPosition,
                onChange = {
                    control.position.value = it
                },
                borders = control.borders,
                lineColor = PrimaryColor1,
                backgroundColor = LightBlue,
                name = control.title,
            )
        }
    }
}

@Composable
fun ApplySettingsButtons(
    eventReceiver: EventReceiver
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.75f),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val buttons = listOf(
            "ok" to EventToSettingsScreenViewModel.ApplySettings,
            "back" to EventToViewModel.Finish
        )

        buttons.map { (buttonCaption, event) ->
            Button(
                {
                    eventReceiver.receiveEvent(
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
