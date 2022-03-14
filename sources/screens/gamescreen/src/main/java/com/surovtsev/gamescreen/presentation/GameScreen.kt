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
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.core.ui.theme.Teal200
import com.surovtsev.core.viewmodel.ErrorDialogPlacer
import com.surovtsev.core.viewmodel.PlaceErrorDialog
import com.surovtsev.core.viewmodel.ScreenStateFlow
import com.surovtsev.finitestatemachine.interfaces.EventReceiver
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamescreen.viewmodel.GameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
import com.surovtsev.gamescreen.viewmodel.helpers.typealiases.GLSurfaceViewCreated
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

    val eventReceiver = viewModel.finiteStateMachine as EventReceiver

    LaunchedEffect(key1 = Unit) {
        viewModel.finishActionHolder.finishAction =
            {
                navController.navigateUp()
            }
        eventReceiver.receiveEvent(
            if (loadGame) {
                EventToGameScreenViewModel.LoadGame()
            } else {
                EventToGameScreenViewModel.NewGame
            }
        )
    }

    GameScreenControls(
        viewModel.screenStateFlow,
        eventReceiver,
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

        errorDialogPlacer.PlaceErrorDialog()

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
    val gameScreenState by screenStateFlow.collectAsState()
    val gameScreenData = gameScreenState.data

    val text = if (gameScreenData !is GameScreenData.GameInProgress) {
        "--"
    } else {
        val fps = gameScreenData.uiGameControls.fpsFlow.collectAsState(0f).value
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
    val gameScreenState by screenStateFlow.collectAsState()

    val gameScreenData = gameScreenState.data
    if (gameScreenState.description !is Description.Idle || gameScreenData !is GameScreenData.GameMenu) {
        return
    }

    val mainMenuButtons = arrayOf(
        "new game" to EventToGameScreenViewModel.NewGame,
        "main menu" to EventToGameScreenViewModel.GoToMainMenu,
    )

    val closeAction: () -> Unit = {
        eventReceiver.receiveEvent(
            EventToGameScreenViewModel.CloseGameMenu
        )
    }

    Dialog(
        onDismissRequest = closeAction,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f),
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
                screenStateFlow,
                eventReceiver,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            GameInfo(
                screenStateFlow
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
        val buttons = arrayOf(
            "1" to EventToGameScreenViewModel.RemoveFlaggedBombs,
            "2" to EventToGameScreenViewModel.RemoveOpenedSlices,
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
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
) {
    val state = screenStateFlow.collectAsState().value

    val stateData = state.data

    // TODO: refactor
    if (stateData !is GameScreenData) {
        return
    }

    val screenData = stateData.rootScreenData()

    if (screenData !is GameScreenData.GameInProgress) {
        return
    }

    val uiGameControlsFlows = screenData.uiGameControls

    val flagged = uiGameControlsFlows.flagging.collectAsState(initial = false).value

    val toggleFlaggingAction = {
        eventReceiver.receiveEvent(
            EventToGameScreenViewModel.ToggleFlagging
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
    screenStateFlow: ScreenStateFlow,
) {
    val state = screenStateFlow.collectAsState().value

    val stateData = state.data

    // TODO: refactor
    if (stateData !is GameScreenData) {
        return
    }

    val screenData = stateData.rootScreenData()

    if (screenData !is GameScreenData.GameInProgress) {
        return
    }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        BombsLeft(screenData.uiGameControls.bombsLeft)
        TimeElapsed(screenData.uiGameControls.timeSpan)
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
    val state by screenStateFlow.collectAsState()

    val screenData = state.data
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
