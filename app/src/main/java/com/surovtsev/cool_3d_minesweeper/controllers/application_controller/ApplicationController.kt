package com.surovtsev.cool_3d_minesweeper.controllers.application_controller

import android.app.Application
import android.content.Intent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.utils.android_view.components.MessagesComponent
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLazyVal
import java.lang.Exception

class ApplicationController() : Application() {
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


    private var _saveController: SaveController? = null

    val saveController: SaveController
        get(): SaveController {
            if (_saveController == null) {
                _saveController = SaveController(this)
            }
            return _saveController!!
        }

    private val dbHelper = MyLazyVal { DBHelper(instance!!) }
    val settingsDBQueries = MyLazyVal { SettingsDBQueries(dbHelper.value) }
    val rankingDBQueries = MyLazyVal { RankingDBQueries(dbHelper.value) }

    init {
        if (instance != null) {
            throw Exception("Application controller instanced")
        }
        instance = this
    }
}

typealias LogSceneDelegate = () -> Unit
