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
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.model_views.RemoveMarkedBombsAction
import com.surovtsev.cool_3d_minesweeper.model_views.RemoveZeroBordersAction
import com.surovtsev.cool_3d_minesweeper.model_views.SetMarkingAction
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.*
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.OpenGLInfoHelper
import javax.inject.Inject
import javax.inject.Named

class GameActivity: ComponentActivity() {
    companion object {
        const val LoadGame = "LoadGame"
    }

    @Inject
    lateinit var modelView: GameActivityModelView

    @Inject
    lateinit var gLSurfaceView: GLSurfaceView

    @Inject
    @Named(GameViewEventsNames.Marking)
    lateinit var markingEvent: MarkingEvent

    @Inject
    @Named(GameViewEventsNames.ElapsedTime)
    lateinit var elapsedTimeEvent: ElapsedTimeEvent

    @Inject
    @Named(GameViewEventsNames.BombsLeft)
    lateinit var bombsLeftEvent: BombsLeftEvent

    @Inject
    @Named(GameViewEventsNames.ShowDialog)
    lateinit var showDialogEvent: ShowDialogEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenGLInfoHelper.isSupportEs2(this)) {
            Toast.makeText(this
                , "This device does not support OpenGL ES 2.0"
                , Toast.LENGTH_LONG).show()
            return
        }

        val loadGame = intent.getBooleanExtra(LoadGame, false)

        daggerComponentsHolder.createAndGetGameComponent(
            loadGame
        ).inject(this)

        modelView.prepareGlSurfaceView(gLSurfaceView)

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
                    Row(

                    ) {
                        Controls(
                            markingEvent,
                            modelView::setMarking,
                            bombsLeftEvent,
                            elapsedTimeEvent,
                            modelView::removeMarkedBombs,
                            modelView::removeZeroBorders
                        )
                    }
                }
                GameStatusDialog(
                    showDialogEvent,
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
    markingEvent: MarkingEvent,
    setMarkingAction: SetMarkingAction,
    bombsLeftEvent: BombsLeftEvent,
    elapsedTimeEvent: ElapsedTimeEvent,
    removeMarkedBombsAction: RemoveMarkedBombsAction,
    removeZeroBordersAction: RemoveZeroBordersAction
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(2f),
            Arrangement.Center
        ) {
            ControlButtons(
                removeMarkedBombsAction,
                removeZeroBordersAction
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            ControlCheckBox(markingEvent, setMarkingAction)
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            GameInfo(bombsLeftEvent, elapsedTimeEvent)
        }
    }
}

@Composable
fun ControlButtons(
    removeMarkedBombsAction: RemoveMarkedBombsAction,
    removeZeroBordersAction: RemoveZeroBordersAction
) {
    Row() {
        Button(
            onClick = removeMarkedBombsAction,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("V")
        }
        Button(
            onClick = removeZeroBordersAction,
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
    setMarkingAction: SetMarkingAction
) {
    val checked: Boolean by markingEvent.data.observeAsState(
        markingEvent.defaultValue
    )
    Surface(
        shape = MaterialTheme.shapes.large,
        onClick = { setMarkingAction(!checked) },
    ) {
        Row(
            Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = setMarkingAction
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
    val bombsLeft: Int by bombsLeftEvent.data.observeAsState(
        bombsLeftEvent.defaultValue
    )
    Text(
        bombsLeft.toString()
    )
}

@Composable
fun TimeElapsed(
    elapsedTimeEvent: ElapsedTimeEvent
) {
    val elapsed: Long by elapsedTimeEvent.data.observeAsState(
        elapsedTimeEvent.defaultValue
    )
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
    val showDialog: Boolean by showDialogEvent.data.observeAsState(
        showDialogEvent.defaultValue
    )
    if (showDialog) {
        val closeDialogAction = { showDialogEvent.onDataChanged(false) }
        AlertDialog(
            onDismissRequest = closeDialogAction,
            title = { Text(text = "Game status") },
            text = { Text(
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
