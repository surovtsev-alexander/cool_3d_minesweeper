package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.content.Intent
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.RankingActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.SettingsActivity

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivityV2


class MainActivityModelView(
    private val context: Context
) {
    val hasSave = MyLiveData(false)

    val buttonsParameters = arrayOf(
        "load game" to this::loadGame,
        "new game" to this::startNewGame,
        "ranking" to this::openRanking,
        "settings" to this::openSettings,
        "new game v2" to this::startGameV2,
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

    private fun startGameV2() {
        startActivityHelper(
            GameActivityV2::class.java
        )
    }

    fun invalidate() {
        hasSave.onDataChanged(
            ApplicationController.getInstance().saveController.hasData(
                SaveTypes.SaveGameJson
            )
        )
    }
}