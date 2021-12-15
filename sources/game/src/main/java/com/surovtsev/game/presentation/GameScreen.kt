package com.surovtsev.game.presentation

import android.app.Activity
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.surovtsev.core.ui.theme.GrayBackground
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.core.ui.theme.Teal200
import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.game.models.game.interaction.GameControls
import com.surovtsev.game.models.game.interaction.RemoveMarkedBombsControl
import com.surovtsev.game.models.game.interaction.RemoveZeroBordersControl
import com.surovtsev.game.viewmodel.*
import com.surovtsev.utils.gles.helpers.OpenGLInfoHelper
import com.surovtsev.game.viewmodel.helpers.*

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel,
    activity: Activity,
    navController: NavController,
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
            if (viewModel.loadGame) {
                CommandsFromGameScreen.LoadGame
            } else {
                CommandsFromGameScreen.NewGame
            }
        )
    }

    val gLSurfaceView = viewModel.gLSurfaceView
    val gameViewEvents = viewModel.gameScreenEvents
    val gameControls = viewModel.gameControls

    GameScreenControls(
        viewModel,
        gLSurfaceView,
        gameViewEvents,
        gameControls,
        viewModel.gameScreenStateValue,
        viewModel
    )
}

private val pauseResumeButtonWidth = 100.dp

@Composable
fun GameScreenControls(
    viewModel: GameScreenViewModel,
    gLSurfaceView: GLSurfaceView,
    gameScreenEvents: GameScreenEvents,
    gameControls: GameControls,
    gameScreenStateValue: GameScreenStateValue,
    gameScreenCommandsHandler: GameScreenCommandsHandler,
) {
    val gameScreenState by gameScreenStateValue.observeAsState(GameScreenInitialState)
    val gameScreenData = gameScreenState.screenData

    MinesweeperTheme {
        if (gameScreenData is GameScreenData.MainMenu) {
            MainMenu(gameScreenStateValue, gameScreenCommandsHandler)
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    MinesweeperView(gLSurfaceView)
                }
                Row {
                    Controls(
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
                        gameScreenCommandsHandler.handleCommand(
                            CommandsFromGameScreen.OpenMenu
                        )
                    },
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(text = "pause")
                }
            }
            GameStatusDialog(
                gameScreenEvents.showDialogEvent,
                viewModel
            )
        }
    }
}

@Composable
fun MainMenu(
    gameScreenStateValue: GameScreenStateValue,
    gameScreenCommandsHandler: GameScreenCommandsHandler,
) {
    val mainMenuButtons = arrayOf(
        "new game" to CommandsFromGameScreen.NewGame,
        "main menu" to CommandsFromGameScreen.GoToMainMenu,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                modifier = Modifier
                    .width(pauseResumeButtonWidth),
                onClick = {
                    gameScreenCommandsHandler.handleCommand(
                        CommandsFromGameScreen.CloseMenu
                    )
                },
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = "resume")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)
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
                                gameScreenCommandsHandler.handleCommand(command)
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
}

@Composable
fun MinesweeperView(
    gLSurfaceView: GLSurfaceView
) {
    AndroidView(
        factory = {
            gLSurfaceView
        }
    )
}

@Composable
fun Controls(
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
                gameScreenEvents.bombsLeftEvent,
                gameScreenEvents.elapsedTimeEvent
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
            onClick = { removeZeroBordersControl.update() },
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
    bombsLeftEvent: BombsLeftEvent,
    elapsedTimeEvent: ElapsedTimeEvent
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        BombsLeft(bombsLeftEvent)
        TimeElapsed(elapsedTimeEvent)
    }
}

@Composable
fun BombsLeft(
    bombsLeftEvent: BombsLeftEvent
) {
    val bombsLeft: Int by bombsLeftEvent.run {
        data.observeAsState(defaultValue)
    }
    Text(
        bombsLeft.toString()
    )
}

@Composable
fun TimeElapsed(
    elapsedTimeEvent: ElapsedTimeEvent
) {
    val elapsed: Long by elapsedTimeEvent.run {
        data.observeAsState(defaultValue)
    }
    Text(
        DateUtils.formatElapsedTime(
            elapsed / 1000,
        )
    )
}

@Composable
fun GameStatusDialog(
    showDialogEvent: ShowDialogEvent,
    viewModel: GameScreenViewModel
) {
    val showDialog: Boolean by showDialogEvent.run {
        data.observeAsState(defaultValue)
    }
    if (!showDialog) {
        return
    }
    val lastWinPlaceEvent = viewModel.gameScreenEvents.lastWinPlaceEvent
    val lastWinPlace: Place by lastWinPlaceEvent.run {
        data.observeAsState(defaultValue)
    }
    val gameStatus = viewModel.minesweeperController.gameLogic.gameLogicStateHelper.gameStatus
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
            /* TODO: replace with gameStatusEvent */
            text = text
        ) },

        confirmButton = {
            Button(
                onClick = closeDialogAction
            ) {
                Text(text = "Ok")
            }
        }
    )
}
