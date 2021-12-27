package com.surovtsev.gamescreen.presentation

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.text.format.DateUtils
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.core.ui.theme.Teal200
import com.surovtsev.core.viewmodel.PlaceErrorDialog
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamescreen.viewmodel.*
import com.surovtsev.utils.gles.helpers.OpenGLInfoHelper
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

    val commandHandler = viewModel as GameScreenCommandHandler

    LaunchedEffect(key1 = Unit) {
        viewModel.finishAction = { navController.navigateUp() }
        commandHandler.handleCommand(
            if (loadGame) {
                CommandFromGameScreen.LoadGame
            } else {
                CommandFromGameScreen.NewGame
            }
        )
    }

    GameScreenControls(
        viewModel.state,
        commandHandler,
        context,
        viewModel::initGLSurfaceView,
        viewModel as GameScreenErrorDialogPlacer,
    )
}

private val pauseResumeButtonWidth = 100.dp

@Composable
fun GameScreenControls(
    stateValue: GameScreenStateValue,
    commandHandler: GameScreenCommandHandler,
    context: Context,
    glSurfaceViewCreated: GLSurfaceViewCreated,
    errorDialogPlacer: GameScreenErrorDialogPlacer,
) {
    MinesweeperTheme {

        errorDialogPlacer.PlaceErrorDialog()

        GameView(
            stateValue,
            commandHandler,
            glSurfaceViewCreated,
            context,
        )

        GameMenu(
            stateValue,
            commandHandler
        )

        GameStatusDialog(
            stateValue,
            commandHandler,
        )
    }
}


@Composable
fun GameView(
    stateValue: GameScreenStateValue,
    commandHandler: GameScreenCommandHandler,
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
                stateValue,
                commandHandler,
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
                stateValue
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
                commandHandler.handleCommand(
                    CommandFromGameScreen.OpenGameMenu
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
    stateValue: GameScreenStateValue,
) {
    val gameScreenState by stateValue.observeAsState(GameScreenInitialState)
    val gameScreenData = gameScreenState.screenData

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
    stateValue: GameScreenStateValue,
    commandHandler: GameScreenCommandHandler,
) {
    val gameScreenState by stateValue.observeAsState(GameScreenInitialState)
    val gameScreenData = gameScreenState.screenData
    if (gameScreenData !is GameScreenData.GameMenu) {
        return
    }

    val mainMenuButtons = arrayOf(
        "new game" to CommandFromGameScreen.NewGame,
        "main menu" to CommandFromGameScreen.GoToMainMenu,
    )

    val closeAction = {
        commandHandler.handleCommand(CommandFromGameScreen.CloseGameMenu)
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
            mainMenuButtons.map { (name, command) ->
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
                            commandHandler.handleCommand(command)
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
    stateValue: GameScreenStateValue,
    commandHandler: GameScreenCommandHandler,
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
                commandHandler,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
        ) {
            ControlCheckBox(
                stateValue,
                commandHandler,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            GameInfo(
                stateValue
            )
        }
    }
}

@Composable
fun ControlButtons(
    commandHandler: GameScreenCommandHandler,
) {
    Row (
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val buttons = arrayOf(
            "1" to CommandFromGameScreen.RemoveFlaggedBombs,
            "2" to CommandFromGameScreen.RemoveOpenedSlices,
        )
        buttons.map { (buttonCaption, commandFromScreen) ->
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = { commandHandler.handleCommand(commandFromScreen) }
            ) {
                Text(text = buttonCaption)
            }
        }
    }
}

@Composable
fun ControlCheckBox(
    stateValue: GameScreenStateValue,
    commandHandler: GameScreenCommandHandler,
) {
    val state = stateValue.observeAsState(
        GameScreenInitialState
    ).value

    val screenData = state.screenData

    if (screenData !is GameScreenData.GameInProgress) {
        return
    }

    val uiGameControlsFlows = screenData.uiGameControls

    val flagged = uiGameControlsFlows.flagging.collectAsState(initial = false).value

    val toggleFlaggingAction = {
        commandHandler.handleCommand(
            CommandFromGameScreen.ToggleFlagging
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
    stateValue: GameScreenStateValue,
) {
    val state = stateValue.observeAsState(
        GameScreenInitialState
    ).value

    val screenData = state.screenData

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
        DateUtils.formatElapsedTime(
            elapsed / 1000,
        )
    )
}

@Composable
fun GameStatusDialog(
    stateValue: GameScreenStateValue,
    commandHandler: GameScreenCommandHandler,
) {
    val state by stateValue.observeAsState(GameScreenInitialState)

    val screenData = state.screenData
    if (screenData !is GameScreenData.GameInProgress) {
        return
    }

    val uiGameStatus = screenData.uiGameControls.uiGameStatus.collectAsState(initial = UIGameStatus.Unimportant).value
    if (uiGameStatus is UIGameStatus.Unimportant) {
        return
    }

    val closeDialogAction = {
        commandHandler.handleCommand(
            CommandFromGameScreen.CloseGameStatusDialog
        )
    }

    val text = if (uiGameStatus is UIGameStatus.Win) {
        "Win\nplace: ${uiGameStatus.place}"
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
                    commandHandler.handleCommand(
                        CommandFromGameScreen.NewGame
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
