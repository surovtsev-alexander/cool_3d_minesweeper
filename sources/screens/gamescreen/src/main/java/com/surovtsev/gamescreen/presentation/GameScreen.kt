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
import com.surovtsev.core.viewmodel.*
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatus
import com.surovtsev.gamescreen.models.game.interaction.RemoveMarkedBombsControl
import com.surovtsev.gamescreen.models.game.interaction.RemoveZeroBordersControl
import com.surovtsev.gamescreen.viewmodel.*
import com.surovtsev.gamescreen.viewmodel.helpers.GameScreenEvents
import com.surovtsev.gamescreen.viewmodel.helpers.MarkingEvent
import com.surovtsev.gamescreen.viewmodel.helpers.Place
import com.surovtsev.utils.gles.helpers.OpenGLInfoHelper
import com.surovtsev.utils.timers.TimeSpanFlow

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
        viewModel.finishAction = { navController.navigateUp() }
        viewModel.handleCommand(
            if (loadGame) {
                CommandFromGameScreen.LoadGame
            } else {
                CommandFromGameScreen.NewGame
            }
        )
    }

    GameScreenControls(
        context,
        viewModel,
//        viewModel.gLSurfaceView!!,
        viewModel
    )
}

private val pauseResumeButtonWidth = 100.dp

@Composable
fun GameScreenControls(
    context: Context,
    viewModel: GameScreenViewModel,
    commandHandler: GameScreenCommandHandler,
) {
    MinesweeperTheme {

        val state = viewModel.state

        viewModel.PlaceErrorDialog()

        GameView(
            state,
            viewModel,
            context,
            commandHandler,
        )

        GameMenu(
            state,
            commandHandler
        )

        GameStatusDialog(
            viewModel,
            commandHandler,
        )
    }
}


@Composable
fun GameView(
    stateValue: GameScreenStateValue,
    viewModel: GameScreenViewModel,
    context: Context,
    commandHandler: GameScreenCommandHandler,
) {
    val gameScreenState by stateValue.observeAsState(GameScreenInitialState)
    val gameScreenData = gameScreenState.screenData

    if (gameScreenData is ScreenData.InitializationIsNotFinished) {
        return
    }

    val gameComponent = viewModel.gameComponent ?: return
    val timeSpan = viewModel.timeSpanComponent?.timeSpan ?: return
    val gameControls = gameComponent.gameControls
    val gameScreenEvents = gameComponent.gameScreenEvents

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ) {
            MinesweeperView(context, viewModel::initGLSurfaceView)
        }
        Row {
            Controls(
                gameComponent.bombsLeftFlow,
                timeSpan.timeSpanFlow,
                gameScreenEvents,
                gameControls.removeMarkedBombsControl,
                gameControls.removeZeroBordersControl
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Button(
            modifier = Modifier
                .width(pauseResumeButtonWidth),
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
    bombsLeftFlow: BombsLeftFlow,
    timeSpanFlow: TimeSpanFlow,
    gameScreenEvents: GameScreenEvents,
    removeMarkedBombsControl: RemoveMarkedBombsControl,
    removeZeroBordersControl: RemoveZeroBordersControl
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
                removeMarkedBombsControl,
                removeZeroBordersControl
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
        ) {
            ControlCheckBox(gameScreenEvents.markingEvent)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            GameInfo(
                bombsLeftFlow,
                timeSpanFlow
            )
        }
    }
}

@Composable
fun ControlButtons(
    removeMarkedBombsControl: RemoveMarkedBombsControl,
    removeZeroBordersControl: RemoveZeroBordersControl
) {
    Row (
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { removeMarkedBombsControl.update() },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("V")
        }
        Button(
            onClick = {
                removeZeroBordersControl.update()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("O")
        }
    }
}

@Composable
fun ControlCheckBox(
    markingEvent: MarkingEvent,
) {
    val checked: Boolean by markingEvent.run {
        data.observeAsState(defaultValue)
    }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clickable { markingEvent.onDataChanged(!checked) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    markingEvent.onDataChanged(it)
                },
                modifier = Modifier.weight(1f)
            )
            Text(
                "marking"
            )
        }
    }
}

@Composable
fun GameInfo(
    bombsLeftFlow: BombsLeftFlow,
    timeSpanFlow: TimeSpanFlow,
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        BombsLeft(bombsLeftFlow)
        TimeElapsed(timeSpanFlow)
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
    viewModel: GameScreenViewModel,
    commandHandler: GameScreenCommandHandler,
) {
    val state by viewModel.state.observeAsState(GameScreenInitialState)

    if (state.screenData !is GameScreenData.GameInProgress) {
        return
    }

    val gameComponent = viewModel.gameComponent ?: return

    val showDialogEvent = gameComponent.gameScreenEvents.showDialogEvent
    val showDialog: Boolean by showDialogEvent.run {
        data.observeAsState(defaultValue)
    }
    if (!showDialog) {
        return
    }


    val lastWinPlaceEvent = gameComponent.gameScreenEvents.lastWinPlaceEvent
    val lastWinPlace: Place by lastWinPlaceEvent.run {
        data.observeAsState(defaultValue)
    }
    val gameStatus = gameComponent.minesweeperController.gameLogic.gameLogicStateHelper.gameStatus()
    val win = gameStatus == GameStatus.Win
    if (win && lastWinPlace == Place.NoPlace) {
        viewModel.requestLastWinPlace()
        return
    }

    val closeDialogAction = {
        showDialogEvent.onDataChanged(false)
        lastWinPlaceEvent.onDataChanged(Place.NoPlace)
    }

    val place = if (win) lastWinPlace else Place.NoPlace

    val text = "$gameStatus ${ if (place is Place.WinPlace) "\nplace: ${place.place + 1}" else ""}"
    AlertDialog(
        onDismissRequest = closeDialogAction,
        title = { Text(text = "Game status") },
        text = { Text(
            text = text
        ) },
        dismissButton = {
            Button(
                onClick = {
                    showDialogEvent.onDataChanged(false)
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
