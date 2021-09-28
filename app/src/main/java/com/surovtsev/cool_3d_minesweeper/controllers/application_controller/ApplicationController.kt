package com.surovtsev.cool_3d_minesweeper.controllers.application_controller

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder.DaggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.utils.android_view.components_unused.MessagesComponent
import java.lang.Exception

class ApplicationController : Application() {
    lateinit var daggerComponentsHolder: DaggerComponentsHolder

    override fun onCreate() {
        AndroidThreeTen.init(this)

        super.onCreate()

        daggerComponentsHolder = DaggerComponentsHolder(this)
    }

    companion object {
        private var instance: ApplicationController? = null

        fun getInstance() = instance!!

        fun tryToAddMessageToComponent(msg: String) =
            instance!!.messagesComponent?.addMessageUI(msg)

        private var startingActivity = false

        fun startingActivityCode(action: () -> Unit) {
            if (startingActivity) return
            startingActivity = true
            action()
        }

        fun activityStarted() { startingActivity = false }
    }

    var messagesComponent: MessagesComponent? = null

    var logScene: LogSceneDelegate? = null

    init {
        if (instance != null) {
            throw Exception("Application controller instanced")
        }
        instance = this
    }
}

typealias LogSceneDelegate = () -> Unit

val Context.daggerComponentsHolder:DaggerComponentsHolder
    get() = when (this) {
        is ApplicationController -> daggerComponentsHolder
        else -> this.applicationContext.daggerComponentsHolder
    }
