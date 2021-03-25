package com.surovtsev.cool_3d_minesweeper.activities

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.GameRenderer
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    var _game_renderer: GameRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

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
            var previousX = 0f
            var previousY = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) {
                    return false
                }

                val action = event.action

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        previousX = event.x
                        previousY = event.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val currX = event.x
                        val currY = event.y

                        val deltaX = currX - previousX
                        val deltaY = currY - previousY

                        previousX = currX
                        previousY = currY

                        glsv_main.queueEvent(object: Runnable {
                            override fun run() {
                                game_renderer._touchHandler.handleTouchDrag(
                                    deltaX, deltaY
                                )
                            }
                        })
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

}