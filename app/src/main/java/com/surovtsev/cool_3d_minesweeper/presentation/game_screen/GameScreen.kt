package com.surovtsev.cool_3d_minesweeper.presentation.game_screen

import android.app.Activity
import android.opengl.GLSurfaceView
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.GameActivityViewModel
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.helpers.*
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.RemoveMarkedBombsControl
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.RemoveZeroBordersControl
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.Test_composeTheme
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.OpenGLInfoHelper

const val LoadGameParameterName = "load_game"

@Composable
fun GameScreen(
    gameComponent: GameComponent,
    activity: Activity
) {
    if (!OpenGLInfoHelper.isSupportEs2(activity)) {
        Toast.makeText(
            activity,
            "This device does not support OpenGL ES 2.0",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    val viewModel = gameComponent.gameActivityViewModel
    val gLSurfaceView = gameComponent.gLSurfaceView
    val gameViewEvents = gameComponent.gameViewEvents
    val gameControls = gameComponent.gameControls

    val x = viewModel.minesweeperController.scene.gameControls.markOnShortTapControl

    viewModel.prepareGlSurfaceView()

    GameScreenControls(
        viewModel,
        gLSurfaceView,
        gameViewEvents,
        gameControls
    )
}

@Composable
fun GameScreenControls(
    viewModel: GameActivityViewModel,
    gLSurfaceView: GLSurfaceView,
    gameViewEvents: GameViewEvents,
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
                    gameViewEvents,
                    gameControls.removeMarkedBombsControl,
                    gameControls.removeZeroBordersControl
                )
            }
        }
        GameStatusDialog(
            gameViewEvents.showDialogEvent,
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
    gameViewEvents: GameViewEvents,
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
            ControlCheckBox(gameViewEvents.markingEvent)
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            GameInfo(
                gameViewEvents.bombsLeftEvent,
                gameViewEvents.elapsedTimeEvent
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ControlCheckBox(
    markingEvent: MarkingEvent,
) {
    val checked: Boolean by markingEvent.run {
        data.observeAsState(defaultValue)
    }
    Surface(
        shape = MaterialTheme.shapes.large,
        onClick = { markingEvent.onDataChanged(!checked) },
    ) {
        Row(
            Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    markingEvent.onDataChanged(it)
                }
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
    viewModel: GameActivityViewModel
) {
    val showDialog: Boolean by showDialogEvent.run {
        data.observeAsState(defaultValue)
    }
    if (showDialog) {
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
}
