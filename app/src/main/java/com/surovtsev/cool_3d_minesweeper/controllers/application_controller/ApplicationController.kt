package com.surovtsev.cool_3d_minesweeper.controllers.application_controller

import android.app.Application
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.utils.android_view.components.MessagesComponent
import java.lang.Exception

class ApplicationController() : Application() {
    companion object {
        private var privateInstance: ApplicationController? = null
        val instance: ApplicationController by lazy {
            privateInstance!!
        }

        fun try_to_add_message_to_component(msg: String) =
            instance.messagesComponent?.addMessageUI(msg)
    }

    var messagesComponent: MessagesComponent? = null

    var logScene: LogSceneDelegate? = null

    val saveController: SaveController by lazy {
        SaveController(this)
    }

    init {
        if (privateInstance != null) {
            throw Exception("Application controller instanced")
        }
        privateInstance = this
    }
}

typealias LogSceneDelegate = () -> Unit
