package com.surovtsev.cool_3d_minesweeper.view.activities

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.GameRenderer
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.util.Timer
import glm_.vec2.Vec2
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.abs

class GameActivity : AppCompatActivity() {
    var _game_renderer: GameRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        ApplicationController.instance!!.messagesComponent = mmc_main
        ApplicationController.instance!!.messagesComponent!!.addMessage("started")

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
        val game_renderer = GameRenderer(this)
        _game_renderer = game_renderer
        glsv_main.setRenderer(_game_renderer)

        glsv_main.setOnTouchListener(object: View.OnTouchListener {
            var prev = Vec2()
            val mTimer = Timer()
            val mMaxClickTimeMs = 100L
            var mMovingDistance = 0f
            val mMovindThreshold = 10

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) {
                    return false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        prev = Vec2(event.x, event.y)
                        mTimer.push()
                        mMovingDistance = 0f
                    }
                    MotionEvent.ACTION_MOVE -> {
                        var curr = Vec2(event.x, event.y)

                        val delta = curr - prev

                        glsv_main.queueEvent(object: Runnable {
                            val p = prev
                            val c = curr
                            override fun run() {
                                game_renderer.mScene?.mCameraInfo?.mMoveHandler?.handleTouchDrag(
                                    p, c
                                )
                            }
                        })

                        mMovingDistance = abs(delta[0]) + abs(delta[1])

                        prev = curr
                    }
                    MotionEvent.ACTION_UP -> {
                        mTimer.push()
                        val moved = mMovingDistance >= mMovindThreshold ||  mTimer.diff() >= mMaxClickTimeMs
                        if (!moved) {
                            if (LoggerConfig.LOG_GAME_ACTIVITY_ACTIONS) {
                                ApplicationController.instance!!.messagesComponent!!.addMessageUI("clicked")
                            }
                            val curr = Vec2(event.x, event.y)
                            game_renderer.mScene?.mClickHandler?.handleClick(curr)
                        }
                    }
                }
                return true
            }
        })
    }

    override fun onPause() {
        super.onPause()

        if (_game_renderer != null) {
            glsv_main.onPause()
        }
    }

    override fun onResume() {
        super.onResume()

        if (_game_renderer != null) {
            glsv_main.onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        ApplicationController.instance!!.messagesComponent = null
    }

}