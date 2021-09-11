package com.surovtsev.cool_3d_minesweeper.controllers.application_controller

import android.app.Application
import android.content.Intent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.utils.android_view.components.MessagesComponent
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

    private var _dbHelper: DBHelper? = null
    private var _settingsDBQueries: SettingsDBQueries? = null
    private var _rankingDBQueries: RankingDBQueries? = null

    val dbHelper: DBHelper
        get(): DBHelper {
            if (_dbHelper == null) {
                _dbHelper = DBHelper(instance!!)
            }
            return _dbHelper!!
        }

    val settingsDBQueries: SettingsDBQueries
        get(): SettingsDBQueries {
            if (_settingsDBQueries == null) {
                _settingsDBQueries = SettingsDBQueries(dbHelper)
            }
            return _settingsDBQueries!!
        }

    val rankingDBQueries: RankingDBQueries
        get(): RankingDBQueries {
            if (_rankingDBQueries == null) {
                _rankingDBQueries = RankingDBQueries(dbHelper)
            }
            return _rankingDBQueries!!
        }

    init {
        if (instance != null) {
            throw Exception("Application controller instanced")
        }
        instance = this
    }
}

typealias LogSceneDelegate = () -> Unit
