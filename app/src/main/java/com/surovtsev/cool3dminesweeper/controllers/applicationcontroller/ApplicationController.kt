package com.surovtsev.cool3dminesweeper.controllers.applicationcontroller

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.surovtsev.cool3dminesweeper.dagger.app.AppComponent
import com.surovtsev.cool3dminesweeper.dagger.app.DaggerAppComponent
import logcat.AndroidLogcatLogger

class ApplicationController : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        AndroidThreeTen.init(this)

        // Log all priorities in debug builds, no-op in release builds.
        AndroidLogcatLogger.installOnDebuggableApp(this)
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is ApplicationController -> appComponent
        else -> applicationContext.appComponent
    }
