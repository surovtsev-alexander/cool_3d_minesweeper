package com.surovtsev.cool_3d_minesweeper.activities

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    var rendererSet = false

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
        glsv_main.setRenderer(GameRenderer(this))
        rendererSet = true
    }

    override fun onPause() {
        super.onPause()

        if (rendererSet) {
            glsv_main.onPause()
        }
    }

    override fun onResume() {
        super.onResume()

        if (rendererSet) {
            glsv_main.onResume()
        }
    }

}