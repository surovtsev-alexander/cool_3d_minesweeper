package com.surovtsev.cool3dminesweeper.presentation.gamescreen

import android.app.Activity
import android.opengl.GLSurfaceView
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.GameScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.*
import com.surovtsev.cool3dminesweeper.models.game.interaction.GameControls
import com.surovtsev.cool3dminesweeper.models.game.interaction.RemoveMarkedBombsControl
import com.surovtsev.cool3dminesweeper.models.game.interaction.RemoveZeroBordersControl
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.Test_composeTheme
import com.surovtsev.cool3dminesweeper.utils.gles.helpers.OpenGLInfoHelper

const val LoadGameParameterName = "load_game"

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel,
    activity: Activity
) {
    if (!OpenGLInfoHelper.isSupportEs2(activity)) {
        Text(
            text = "This device does not support OpenGL ES 2.0",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val gLSurfaceView = viewModel.gLSurfaceView
    val gameViewEvents = viewModel.gameScreenEvents
    val gameControls = viewModel.gameControls

    GameScreenControls(
        viewModel,
        gLSurfaceView,
        gameViewEvents,
        gameControls
    )
}

@Composable
fun GameScreenControls(
    viewModel: GameScreenViewModel,
    gLSurfaceView: GLSurfaceView,
    gameScreenEvents: GameScreenEvents,
    gameControls: GameControls
) {
    Test_composeTheme {
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
        GameStatusDialog(
            gameScreenEvents.showDialogEvent,
            viewModel
        )
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(2f),
            Arrangement.Center
        ) {
            ControlButtons(
                removeMarkedBombsControl,
                removeZeroBordersControl
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            ControlCheckBox(gameScreenEvents.markingEvent)
        }
        Column(
            modifier = Modifier.weight(1f)
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
    Row {
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
        modifier = Modifier.clickable { markingEvent.onDataChanged(!checked) }
    ) {
        Row(
            Modifier.fillMaxWidth(),
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
    val closeDialogAction = { showDialogEvent.onDataChanged(false) }
    AlertDialog(
        onDismissRequest = closeDialogAction,
        title = { Text(text = "Game status") },
        text = { Text(
            /* TODO: replace with gameStatusEvent */
            text = viewModel.minesweeperController.gameLogic.gameLogicStateHelper.gameStatus.toString()
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