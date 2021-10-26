package com.surovtsev.cool3dminesweeper.controllers.applicationcontroller

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger

@HiltAndroidApp
class ApplicationController : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        // Log all priorities in debug builds, no-op in release builds.
        AndroidLogcatLogger.installOnDebuggableApp(this)
    }
}
