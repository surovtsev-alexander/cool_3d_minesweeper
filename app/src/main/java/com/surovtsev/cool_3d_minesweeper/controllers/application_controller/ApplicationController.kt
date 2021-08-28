package com.surovtsev.cool_3d_minesweeper.controllers.application_controller

import android.app.Application
import com.surovtsev.cool_3d_minesweeper.utils.android_view.components.MessagesComponent
import java.lang.Exception

class ApplicationController: Application {
    companion object {
        var instance: ApplicationController? = null

        fun try_to_add_message_to_component(msg: String) =
            instance?.messagesComponent?.addMessageUI(msg)
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
