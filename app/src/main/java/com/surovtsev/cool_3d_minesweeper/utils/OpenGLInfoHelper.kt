package com.surovtsev.cool_3d_minesweeper.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build

object OpenGLInfoHelper {
    fun isSupportEs2(activity: Activity): Boolean {
        val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo

        val fingerprint = Build.FINGERPRINT
        val model = Build.MODEL
        val supportsEs2 =
            configurationInfo.reqGlEsVersion >= 0x20000
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                    && (fingerprint.startsWith("generic")
                    || fingerprint.startsWith("unknown")
                    || model.contains("google_sdk")
                    || model.contains("Emulator")
                    || model.contains("Android SDK built for x86")))

        return supportsEs2
    }
}