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


package com.surovtsev.gamescreen.presentation

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.surovtsev.core.interaction.BombsLeftFlow
import com.surovtsev.core.ui.theme.GrayBackground
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.core.ui.theme.Teal200
import com.surovtsev.finitestatemachine.eventreceiver.EventReceiver
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamescreen.viewmodel.GameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
import com.surovtsev.gamescreen.viewmodel.helpers.typealiases.GLSurfaceViewCreated
import com.surovtsev.templateviewmodel.helpers.errordialog.ErrorDialogPlacer
import com.surovtsev.templateviewmodel.helpers.errordialog.PlaceErrorDialog
import com.surovtsev.templateviewmodel.helpers.errordialog.ScreenStateFlow
import com.surovtsev.utils.gles.helpers.OpenGLInfoHelper
import com.surovtsev.utils.time.elapsedformatter.ElapsedFormatter
import com.surovtsev.utils.timers.async.TimeSpanFlow

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel,
    activity: Activity,
    navController: NavController,
    context: Context,
    loadGame: Boolean,
) {
    if (!OpenGLInfoHelper.isSupportEs2(activity)) {
        Text(
            text = "This device does not support OpenGL ES 2.0",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.finishActionHolder.finishAction =
        {
            navController.navigateUp()
        }
        viewModel.restartFSM(
            if (loadGame) {
                EventToGameScreenViewModel.LoadGame()
            } else {
                EventToGameScreenViewModel.NewGame
            }
        )
    }

    GameScreenControls(
        viewModel.screenStateFlow,
        viewModel.finiteStateMachine.eventReceiver,
        context,
        viewModel::initGLSurfaceView,
        viewModel as ErrorDialogPlacer,
    )
}

@Composable
fun GameScreenControls(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
    context: Context,
    glSurfaceViewCreated: GLSurfaceViewCreated,
    errorDialogPlacer: ErrorDialogPlacer,
) {
    MinesweeperTheme {

        errorDialogPlacer.PlaceErrorDialog(GrayBackground)

        GameView(
            screenStateFlow,
            eventReceiver,
            glSurfaceViewCreated,
            context,
        )

        GameMenu(
            screenStateFlow,
            eventReceiver
        )

        GameStatusDialog(
            screenStateFlow,
            eventReceiver,
        )
    }
}

private val pauseResumeButtonWidth = 100.dp

@Composable
fun GameView(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
    glSurfaceViewCreated: GLSurfaceViewCreated,
    context: Context,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ) {
            MinesweeperView(context, glSurfaceViewCreated)
        }
        Row {
            Controls(
                screenStateFlow,
                eventReceiver,
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            FPSLabel(
                this,
                screenStateFlow
            )
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        Button(
            modifier = Modifier
                .width(pauseResumeButtonWidth)
            ,
            onClick = {
                eventReceiver.receiveEvent(
                    EventToGameScreenViewModel.OpenGameMenuAndSetIdleState
                )
            },
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text(text = "pause")
        }
    }
}

@Composable
fun FPSLabel(
    columnScope: ColumnScope,
    screenStateFlow: ScreenStateFlow,
) {
    val screenState by screenStateFlow.collectAsState()
    val screenData = screenState.data

    if (screenData !is GameScreenData) {
        return
    }

    val gameInProgress = screenData.rootScreenData() as? GameScreenData.GameInProgress

    val text = if (gameInProgress == null) {
        "--"
    } else {
        val fps = gameInProgress.uiGameControls.fpsFlow.collectAsState(0f).value
        fps.toInt().toString().padStart(4)
    }

    columnScope.apply {
        Text(
            text = text,
            modifier = Modifier
                .width(50.dp)
        )
    }
}


@Composable
fun GameMenu(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
) {
    val screenState by screenStateFlow.collectAsState()

    val screenData = screenState.data
    if (screenState.description !is Description.Idle || screenData !is GameScreenData.GameMenu) {
        return
    }

    val closeGameMenuEvent = EventToGameScreenViewModel.CloseGameMenu

    val mainMenuButtons = listOf(
        "resume" to closeGameMenuEvent,
        "new game" to EventToGameScreenViewModel.NewGame,
        "main menu" to EventToGameScreenViewModel.GoToMainMenu,
    )

    val closeAction: () -> Unit = {
        eventReceiver.receiveEvent(
            closeGameMenuEvent
        )
    }

    Dialog(
        onDismissRequest = closeAction,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Paused",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            }
            mainMenuButtons.map { (name, event) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        onClick = {
                            eventReceiver.receiveEvent(event)
                        },
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text(text = name)
                    }
                }
            }
        }
    }
}

@Composable
fun MinesweeperView(
    context: Context,
    glSurfaceViewCreated: GLSurfaceViewCreated,
) {
    AndroidView(
        factory = {
            GLSurfaceView(context).apply {
                glSurfaceViewCreated.invoke(
                    this
                )
            }
        }
    )
}

@Composable
fun Controls(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
) {
    val screenState = screenStateFlow.collectAsState().value

    val screenData = screenState.data
    if (screenData !is GameScreenData) {
        return
    }

    val gameInProgress = screenData.rootScreenData() as? GameScreenData.GameInProgress

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Teal200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
        ) {
            ControlButtons(
                eventReceiver,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
        ) {
            ControlCheckBox(
                gameInProgress,
                eventReceiver,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            GameInfo(
                gameInProgress
            )
        }
    }
}

@Composable
fun ControlButtons(
    eventReceiver: EventReceiver,
) {
    Row (
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val buttons = listOf(
            "1" to EventToGameScreenViewModel.EventWithoutSettingLoadingStateBeforeProcessing.RemoveFlaggedBombs,
            "2" to EventToGameScreenViewModel.EventWithoutSettingLoadingStateBeforeProcessing.RemoveOpenedSlices,
        )
        buttons.map { (buttonCaption, event) ->
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = { eventReceiver.receiveEvent(event) }
            ) {
                Text(text = buttonCaption)
            }
        }
    }
}

@Composable
fun ControlCheckBox(
    gameInProgress: GameScreenData.GameInProgress?,
    eventReceiver: EventReceiver,
) {
    if (gameInProgress == null) {
        return
    }

    val flagged = gameInProgress
        .uiGameControls
        .flagging
        .collectAsState(initial = false)
        .value

    val toggleFlaggingAction = {
        eventReceiver.receiveEvent(
            EventToGameScreenViewModel.EventWithoutSettingLoadingStateBeforeProcessing.ToggleFlagging
        )
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clickable { toggleFlaggingAction() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = flagged,
                onCheckedChange = null,
                modifier = Modifier.weight(1f),
            )
            Text(
                "flagging"
            )
        }
    }
}

@Composable
fun GameInfo(
    gameInProgress: GameScreenData.GameInProgress?,
) {

    val uiGameControls = gameInProgress?.uiGameControls ?: return

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        BombsLeft(uiGameControls.bombsLeft)
        TimeElapsed(uiGameControls.timeSpan)
    }
}

@Composable
fun BombsLeft(
    bombsLeftFlow: BombsLeftFlow
) {
    val bombsLeft = bombsLeftFlow.collectAsState(initial = 0).value
    Text(
        bombsLeft.toString()
    )
}

@Composable
fun TimeElapsed(
    timeSpanFlow: TimeSpanFlow
) {
    val elapsed = timeSpanFlow.collectAsState(0).value
    Text(
        ElapsedFormatter.formatElapsedMillis(
            elapsed
        )
    )
}

@Composable
fun GameStatusDialog(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
) {
    val screenState by screenStateFlow.collectAsState()

    val screenData = screenState.data
    if (screenData !is GameScreenData.GameInProgress) {
        return
    }

    val uiGameStatus = screenData.uiGameControls.uiGameStatus.collectAsState(initial = UIGameStatus.Unimportant).value
    if (uiGameStatus is UIGameStatus.Unimportant) {
        return
    }

    val closeDialogAction = {
        eventReceiver.receiveEvent(
            EventToGameScreenViewModel.CloseGameStatusDialog
        )
    }


    val text = if (uiGameStatus is UIGameStatus.Win) {
        "Win\n" +
                "place: ${uiGameStatus.place}\n" +
                "elapsed: ${ElapsedFormatter.formatElapsedMillis(uiGameStatus.elapsed)}"
    } else {
        "Lose"
    }

    AlertDialog(
        onDismissRequest = closeDialogAction,
        title = { Text(text = "Game status") },
        text = { Text(
            text = text
        ) },
        dismissButton = {
            Button(
                onClick = {
                    closeDialogAction.invoke()
                    eventReceiver.receiveEvent(
                        EventToGameScreenViewModel.NewGame
                    )
                }
            ) {
                Text(text = "new game")
            }
        },
        confirmButton = {
            Button(
                onClick = closeDialogAction
            ) {
                Text(text = "Ok")
            }
        }
    )
}
