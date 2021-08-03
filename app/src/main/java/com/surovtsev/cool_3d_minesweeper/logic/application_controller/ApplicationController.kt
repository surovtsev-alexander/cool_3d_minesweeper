package com.surovtsev.cool_3d_minesweeper.logic.application_controller

import android.app.Application
import com.surovtsev.cool_3d_minesweeper.view.components.MessagesComponent
import java.lang.Exception

class ApplicationController: Application {
    companion object {
        var instance: ApplicationController? = null
    }

    constructor() {
        if (instance != null) {
            throw Exception("Application controller instanced")
        }
        instance = this
    }

    var messagesComponent: MessagesComponent? = null

    var logScene: LogSceneDelegate? = null
}

typealias LogSceneDelegate = () -> Unit
