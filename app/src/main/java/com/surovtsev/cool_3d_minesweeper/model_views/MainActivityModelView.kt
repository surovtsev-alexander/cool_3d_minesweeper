package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.content.Intent
import com.surovtsev.cool_3d_minesweeper.utils.live_data.MyLiveData
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.RankingActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.SettingsActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.SettingsActivityV2

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes


class MainActivityModelView(
    private val context: Context
) {
    val hasSave = MyLiveData<Boolean>(false)

    val buttonsParameters = arrayOf(
        "load game" to this::loadGame,
        "new game" to this::startNewGame,
        "ranking" to this::openRanking,
        "settings" to this::openSettings,
        "settingsV2" to this::openSettingsV2,
    )

    fun isLoadGameAction(action: () -> Unit): Boolean =
        action == this::loadGame

    private fun loadGame() {
        startGame(true)
    }

    private fun startNewGame() {
        startGame(false)
    }

    private fun openRanking() {
        startActivityHelper(RankingActivity::class.java)
    }

    private fun openSettings() {
        startActivityHelper(SettingsActivity::class.java)
    }

    private fun openSettingsV2() {
        startActivityHelper(SettingsActivityV2::class.java)
    }

    private fun <T> startActivityHelper(x: Class<T>) {
        context.startActivity(
            Intent(context, x)
        )
    }

    private fun startGame(loadGame: Boolean) {
        val intent = Intent(context, GameActivity::class.java)
        intent.putExtra(GameActivity.LoadGame, loadGame)
        context.startActivity(intent)
    }

    fun invalidate() {
        hasSave.onDataChanged(
            ApplicationController.getInstance().saveController.hasData(
                SaveTypes.SaveGameJson
            )
        )
    }
}