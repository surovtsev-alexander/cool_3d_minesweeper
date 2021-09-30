package com.surovtsev.cool_3d_minesweeper.controllers.application_controller

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder.DaggerComponentsHolder

class ApplicationController : Application() {
    lateinit var daggerComponentsHolder: DaggerComponentsHolder

    override fun onCreate() {
        AndroidThreeTen.init(this)

        super.onCreate()

        daggerComponentsHolder = DaggerComponentsHolder(this)
    }
}

val Context.daggerComponentsHolder:DaggerComponentsHolder
    get() = when (this) {
        is ApplicationController -> daggerComponentsHolder
        else -> this.applicationContext.daggerComponentsHolder
    }
