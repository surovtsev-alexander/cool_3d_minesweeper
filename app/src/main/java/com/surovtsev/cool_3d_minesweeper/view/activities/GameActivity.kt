package com.surovtsev.cool_3d_minesweeper.view.activities

import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
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
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization.ClickAndRotationHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization.MovingHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization.ScalingHelper
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization.TouchHelper
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

        btn_remove_marked_bombs.setOnClickListener(View.OnClickListener { v ->
            gameRenderer?.scene?.removeBombs?.update()
        })

        btn_remove_border_zeros.setOnClickListener( { v->
            gameRenderer?.scene?.removeBorderZeros?.update()
        })

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2 =
            configurationInfo.reqGlEsVersion >= 0x20000
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                    && (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")))

        if (!supportsEs2) {
            Toast.makeText(this
                , "This device does not suppoert OpenGL ES 2.0"
                , Toast.LENGTH_LONG).show()

            return
        }

        glsv_main.setEGLContextClientVersion(2)
        val gR = GameRenderer(this, this, this::updateTime)
        gameRenderer = gR
        glsv_main.setRenderer(gameRenderer)

        glsv_main.setOnTouchListener(object: View.OnTouchListener {
            var prevPointerCount = 0

            val clickAndRotationHelper =
                ClickAndRotationHelper(
                    object :
                        IReceiverCalculator<ITouchReceiver> {
                        override fun getReceiver(): ITouchReceiver? =
                            gR.clickHelper
                    },
                    object :
                        IReceiverCalculator<IRotationReceiver> {
                        override fun getReceiver(): IRotationReceiver? =
                            gR.scene?.cameraInfo?.moveHandler

                    },
                    glsv_main
                )
            val scalingHelper =
                ScalingHelper(
                    object :
                        IReceiverCalculator<IScaleReceiver> {
                        override fun getReceiver(): IScaleReceiver? =
                            gR.scene?.cameraInfo?.moveHandler
                    },
                    object: IReceiverCalculator<IMoveReceiver> {
                        override fun getReceiver(): IMoveReceiver? =
                            gR.scene?.cameraInfo?.moveHandler
                    }
                )
            val movingHelper =
                MovingHelper(
                    object: IReceiverCalculator<IMoveReceiver> {
                        override fun getReceiver(): IMoveReceiver? =
                            gR.scene?.cameraInfo?.moveHandler
                    }
                )

            var currTouchHelper: TouchHelper = clickAndRotationHelper

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) {
                    return false
                }

                val pointerCount = event.pointerCount

                if (pointerCount != prevPointerCount) {
                    if (prevPointerCount != 0) {
                        currTouchHelper.tryToRelease()
                    }

                    prevPointerCount = pointerCount

                    when (pointerCount) {
                        1 -> {
                            currTouchHelper = clickAndRotationHelper
                        }
                        2 -> {
                            currTouchHelper = scalingHelper
                        }
                        else -> {
                            currTouchHelper = movingHelper
                        }
                    }
                }

                currTouchHelper.onTouch(event)

                return true
            }
        })
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