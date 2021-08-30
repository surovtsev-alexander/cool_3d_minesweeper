package com.surovtsev.cool_3d_minesweeper.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.OpenGLInfoHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.my_dialog.MyDialog
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity(), IGameStatusesReceiver {
    private var minesweeperController: MinesweeperController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        updateTime()
        bombCountUpdated()

        if (!OpenGLInfoHelper.isSupportEs2(this)) {
            Toast.makeText(this
                , "This device does not support OpenGL ES 2.0"
                , Toast.LENGTH_LONG).show()
            return
        }

        glsv_main.setEGLContextClientVersion(2)

        minesweeperController = MinesweeperController(
            this,
            this,
            this::updateTime
        )
        glsv_main.setRenderer(minesweeperController!!.gameRenderer)

        assignListeners()
    }

    private fun assignListeners() {
        btn_remove_marked_bombs.setOnClickListener { _ ->
            minesweeperController?.scene?.removeBombs?.update()
        }

        btn_remove_border_zeros.setOnClickListener { _ ->
            minesweeperController?.scene?.removeBorderZeros?.update()
        }

        val touchReceiverCalculator = object: ITouchReceiverCalculator {
            override fun getReceiver(): ITouchReceiver? = minesweeperController?.touchReceiver
        }
        val rotationReceiverCalculator = object: IRotationReceiverCalculator {
            override fun getReceiver(): IRotationReceiver? = minesweeperController?.scene?.moveHandler
        }
        val scaleReceiverCalculator = object: IScaleReceiverCalculator {
            override fun getReceiver(): IScaleReceiver? = minesweeperController?.scene?.moveHandler
        }
        val moveReceiverCalculator = object: IMoveReceiverCalculator {
            override fun getReceiver(): IMoveReceiver? = minesweeperController?.scene?.moveHandler
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

    override fun gameStatusUpdated(newStatus: GameStatus) {
        if (GameStatusHelper.isGameOver(newStatus)) {
            minesweeperController?.gameTimeTicker?.turnOff()

            val gameStatusDialog = MyDialog(newStatus.toString())
            val manager = supportFragmentManager
            gameStatusDialog.show(manager, "gameStatusDialog")

        } else if (GameStatusHelper.isGameInProgress(newStatus)) {
            minesweeperController?.gameTimeTicker?.turnOn()
        }
    }

    override fun bombCountUpdated() {
        lbl_bombs_count.text = (minesweeperController?.gameLogic?.bombsLeft?:0).toString()
    }

    private fun updateTime() {
        val time = minesweeperController?.gameTimeTicker?.getElapsed()?:0
        lbl_time.text = DateUtils.formatElapsedTime( time / 1000)
    }

    override fun onPause() {
        super.onPause()

        if (minesweeperController != null) {
            minesweeperController!!.onPause()
            glsv_main.onPause()
        }

        Log.d("TEST+++", "GameActivity onPause")

    }

    override fun onResume() {
        super.onResume()

        if (minesweeperController != null) {
            minesweeperController!!.onResume()
            glsv_main.onResume()
        }

        Log.d("TEST+++", "GameActivity onResume")
    }

    override fun onDestroy() {
        super.onDestroy()

        minesweeperController?.onDestroy()

        ApplicationController.instance!!.messagesComponent = null

        Log.d("TEST+++", "GameActivity onDestroy")
    }
}