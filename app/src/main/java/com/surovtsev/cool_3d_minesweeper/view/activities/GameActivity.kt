package com.surovtsev.cool_3d_minesweeper.view.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.game_logic.data.GameStatus
import com.surovtsev.cool_3d_minesweeper.game_logic.data.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.game_logic.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.GameRenderer
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.utils.OpenGLInfoHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.ClickAndRotationHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.MovingHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.ScalingHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.TouchHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_listener.receiver.TouchListenerReceiver
import kotlinx.android.synthetic.main.activity_game.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.IllegalStateException

class GameActivity : AppCompatActivity(), IGameStatusesReceiver {
    var gameRenderer: GameRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        updateTime()

//        ApplicationController.instance!!.messagesComponent = mmc_main
        ApplicationController.instance!!.messagesComponent?.addMessage("started")

        btn_remove_marked_bombs.setOnClickListener { _ ->
            gameRenderer?.scene?.removeBombs?.update()
        }

        btn_remove_border_zeros.setOnClickListener { _ ->
            gameRenderer?.scene?.removeBorderZeros?.update()
        }

        if (!OpenGLInfoHelper.isSupportEs2(this)) {
            Toast.makeText(this
                , "This device does not suppoert OpenGL ES 2.0"
                , Toast.LENGTH_LONG).show()
            return
        }

        glsv_main.setEGLContextClientVersion(2)
        val gR = GameRenderer(this, this, this::updateTime)
        gameRenderer = gR
        glsv_main.setRenderer(gameRenderer)

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

    class MyDialog(val msg: String): DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle(msg)
                builder.setPositiveButton("Ok") { dialog, which -> dialog.cancel() }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    override fun gameStatusUpdated(newStatus: GameStatus) {
        if (GameStatusHelper.isGameOver(newStatus)) {
            gameRenderer?.gameTimeTicker?.turnOff()

            doAsync {
                uiThread {
                    val gameStateDialog = MyDialog(newStatus.toString())
                    val manager = supportFragmentManager
                    gameStateDialog.show(manager, "gameStateDialog")
                }
            }
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