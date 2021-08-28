package com.surovtsev.cool_3d_minesweeper.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.game_logic.data.GameStatus
import com.surovtsev.cool_3d_minesweeper.game_logic.data.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.game_logic.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.GameRenderer
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.utils.opengl.helpers.OpenGLInfoHelper
import com.surovtsev.cool_3d_minesweeper.utils.view.my_dialog.MyDialog
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.receiver.TouchListenerReceiver
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity(), IGameStatusesReceiver {
    var gameRenderer: GameRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        updateTime()

        if (!OpenGLInfoHelper.isSupportEs2(this)) {
            Toast.makeText(this
                , "This device does not support OpenGL ES 2.0"
                , Toast.LENGTH_LONG).show()
            return
        }

        glsv_main.setEGLContextClientVersion(2)
        gameRenderer = GameRenderer(this, this, this::updateTime)
        glsv_main.setRenderer(gameRenderer)

        assignListeners()
    }

    private fun assignListeners() {
        val gR = gameRenderer!!

        btn_remove_marked_bombs.setOnClickListener { _ ->
            gameRenderer?.scene?.removeBombs?.update()
        }

        btn_remove_border_zeros.setOnClickListener { _ ->
            gameRenderer?.scene?.removeBorderZeros?.update()
        }

        val touchReceiverCalculator = object: ITouchReceiverCalculator {
            override fun getReceiver(): ITouchReceiver? = gR.clickHelper
        }
        val rotationReceiverCalculator = object: IRotationReceiverCalculator {
            override fun getReceiver(): IRotationReceiver? = gR.scene?.cameraInfo?.moveHandler
        }
        val scaleReceiverCalculator = object: IScaleReceiverCalculator {
            override fun getReceiver(): IScaleReceiver? = gR.scene?.cameraInfo?.moveHandler
        }
        val moveReceiverCalculator = object: IMoveReceiverCalculator {
            override fun getReceiver(): IMoveReceiver? = gR.scene?.cameraInfo?.moveHandler
        }
        val touchListenerReceiver = TouchListenerReceiver(
            glsv_main,
            touchReceiverCalculator,
            rotationReceiverCalculator,
            scaleReceiverCalculator,
            moveReceiverCalculator,
        )
        val touchListener = TouchListener(touchListenerReceiver)
        glsv_main.setOnTouchListener(touchListener)
    }

    override fun onPause() {
        super.onPause()

        if (gameRenderer != null) {
            glsv_main.onPause()
        }
    }

    override fun gameStatusUpdated(newStatus: GameStatus) {
        if (GameStatusHelper.isGameOver(newStatus)) {
            gameRenderer?.gameTimeTicker?.turnOff()

            val gameStatusDialog = MyDialog(newStatus.toString())
            val manager = supportFragmentManager
            gameStatusDialog.show(manager, "gameStatusDialog")

        } else if (GameStatusHelper.isGameStarted(newStatus)) {
            gameRenderer?.gameTimeTicker?.turnOn()
        }
    }

    private fun updateTime() {
        val time = gameRenderer?.gameTimeTicker?.getElapsed()?:0
        lbl_time.text = DateUtils.formatElapsedTime( time / 1000)
    }

    override fun onResume() {
        super.onResume()

        if (gameRenderer != null) {
            glsv_main.onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        ApplicationController.instance!!.messagesComponent = null
    }
}