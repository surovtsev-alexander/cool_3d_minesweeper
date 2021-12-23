package com.surovtsev.cool3dminesweeper.app

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.surovtsev.cool3dminesweeper.dagger.app.AppComponent
import com.surovtsev.cool3dminesweeper.dagger.app.DaggerAppComponent
import com.surovtsev.core.app.Cool3dMinesweeperApplication
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import logcat.AndroidLogcatLogger

class Cool3dMinesweeperApplicationImp : Application(), Cool3dMinesweeperApplication {
    lateinit var appComponent: AppComponent
        private set

    override val appComponentEntryPoint: AppComponentEntryPoint  = appComponent

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
        is Cool3dMinesweeperApplicationImp -> appComponent
        else -> applicationContext.appComponent
    }
