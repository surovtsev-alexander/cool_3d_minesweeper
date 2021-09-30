package com.surovtsev.cool_3d_minesweeper.controllers.application_controller

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder.DaggerComponentsHolderNew

class ApplicationController : Application() {
    lateinit var daggerComponentsHolderNew: DaggerComponentsHolderNew

    override fun onCreate() {
        AndroidThreeTen.init(this)

        super.onCreate()

        daggerComponentsHolderNew = DaggerComponentsHolderNew(this)
    }
//    companion object {
//        private var instance: ApplicationController? = null
//
//        fun getInstance() = instance!!
//
//        fun tryToAddMessageToComponent(msg: String) =
//            instance!!.messagesComponent?.addMessageUI(msg)
//
//        private var startingActivity = false
//
//        fun startingActivityCode(action: () -> Unit) {
//            if (startingActivity) return
//            startingActivity = true
//            action()
//        }
//
//        fun activityStarted() { startingActivity = false }
//    }
//
//    var messagesComponent: MessagesComponent? = null
//
//    var logScene: LogSceneDelegate? = null
//    init {
//        if (instance != null) {
//            throw Exception("Application controller instanced")
//        }
//        instance = this
//    }
}

//typealias LogSceneDelegate = () -> Unit

val Context.daggerComponentsHolderNew:DaggerComponentsHolderNew
    get() = when (this) {
        is ApplicationController -> daggerComponentsHolderNew
        else -> this.applicationContext.daggerComponentsHolderNew
    }
