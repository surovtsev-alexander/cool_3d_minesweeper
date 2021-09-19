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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.appComponent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.GameLogicStateHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.OpenGLInfoHelper

class GameActivity: ComponentActivity() {
    companion object {
        const val LoadGame = "LoadGame"
    }

    private val modelView: GameActivityModelView by lazy {
        GameActivityModelView(
            this, appComponent
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!OpenGLInfoHelper.isSupportEs2(this)) {
            Toast.makeText(this
                , "This device does not support OpenGL ES 2.0"
                , Toast.LENGTH_LONG).show()
            return
        }

        val intent = getIntent()
        val loadGame = intent.getBooleanExtra(LoadGame, false)

        if (loadGame) {
            modelView.minesweeperController.loadGame()
        }

        setContent {
            Test_composeTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        MinesweeperView(modelView)
                    }
                    Row(

                    ) {
                        Controls(modelView)
                    }
                }
                GameStatusDialog(modelView)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        modelView.onPause()
    }

    override fun onResume() {
        super.onResume()
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
    modelView: GameActivityModelView
) {
    val glSurfaceView = remember {
        GLSurfaceView(modelView.context).apply {
            modelView.assignTouchListenerToGLSurfaceView(this)
            modelView.glSurfaceView = this
        }
    }
    AndroidView(
        factory = {
            glSurfaceView.apply {
                setEGLContextClientVersion(2)
                setRenderer(modelView.minesweeperController.gameRenderer)
            }
        }
    )
}

@Composable
fun Controls(
    modelView: GameActivityModelView
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(2f),
            Arrangement.Center
        ) {
            ControlButtons(modelView)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            ControlCheckBox(modelView)
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            GameInfo(modelView)
        }
    }
}

@Composable
fun ControlButtons(
    modelView: GameActivityModelView
) {
    Row() {
        Button(
            onClick = modelView::removeMarkedBombs,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("V")
        }
        Button(
            onClick = modelView::removeZeroBorders,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("O")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ControlCheckBox(
    modelView: GameActivityModelView
) {
    val checked: Boolean by modelView.marking.data.observeAsState(
        modelView.marking.defaultValue
    )
    Surface(
        shape = MaterialTheme.shapes.large,
        onClick = { modelView.setMarking(!checked) },
    ) {
        Row(
            Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = modelView::setMarking
            )
            Text(
                "marking"
            )
        }
    }
}

@Composable
fun GameInfo(
    modelView: GameActivityModelView
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        BombsLeft(modelView)
        TimeElapsed(modelView)
    }
}

@Composable
fun BombsLeft(
    modelView: GameActivityModelView
) {
    val bombsLeft: Int by modelView.bombsLeft.data.observeAsState(
        modelView.bombsLeft.defaultValue
    )
    Text(
        bombsLeft.toString()
    )
}

@Composable
fun TimeElapsed(
    modelView: GameActivityModelView
) {
    val elapsed: Long by modelView.elapsedTime.data.observeAsState(
        modelView.elapsedTime.defaultValue
    )
    Text(
        DateUtils.formatElapsedTime(
            elapsed / 1000,
        )
    )
}

@Composable
fun GameStatusDialog(
    modelView: GameActivityModelView
) {
    val showDialog: Boolean by modelView.showDialog.data.observeAsState(
        modelView.showDialog.defaultValue
    )
    if (showDialog) {
        val closeDialogAction = { modelView.showDialog.onDataChanged(false) }
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
