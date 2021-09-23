package com.surovtsev.cool_3d_minesweeper.views.activities

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.text.format.DateUtils
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.helpers.*
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.RemoveMarkedBombsControl
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.RemoveZeroBordersControl
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.OpenGLInfoHelper
import javax.inject.Inject

class GameActivity: ComponentActivity() {
    companion object {
        const val LoadGame = "LoadGame"
    }

    @Inject
    lateinit var modelView: GameActivityModelView
    @Inject
    lateinit var gLSurfaceView: GLSurfaceView

    @Inject
    lateinit var gameViewEvents: GameViewEvents
    @Inject
    lateinit var gameControls: GameControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenGLInfoHelper.isSupportEs2(this)) {
            Toast.makeText(
                this,
                "This device does not support OpenGL ES 2.0",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val loadGame = intent.getBooleanExtra(LoadGame, false)

        daggerComponentsHolder.createAndGetGameComponent(
            loadGame
        ).inject(this)


        modelView.prepareGlSurfaceView()

        setContent {
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
                    modelView
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        gLSurfaceView.onPause()
        modelView.onPause()
    }

    override fun onResume() {
        super.onResume()
        gLSurfaceView.onResume()
        modelView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        modelView.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (modelView.onKeyDown(keyCode)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
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
                onCheckedChange = markingEvent::onDataChanged
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
    modelView: GameActivityModelView
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
                text = modelView.minesweeperController.gameLogic.gameLogicStateHelper.gameStatus.toString()
            ) },

            confirmButton = {
                Button(
                    onClick = closeDialogAction
                ) {
                    Text(text = "Ok")
                }
            },
        )
    }
}
