package com.surovtsev.cool_3d_minesweeper.model_views.main_activity_model_view

import android.content.Context
import android.content.Intent
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.views.activities.RankingActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.SettingsActivity
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.dagger.app.AppScope
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivity
import javax.inject.Inject
import javax.inject.Named

typealias HasSaveEvent = MyLiveData<Boolean>
typealias ButtonParameter = Pair<String, () -> Unit>
typealias ButtonParameters = Array<ButtonParameter>
typealias IsLoadedGameAction = (() -> Unit) -> Boolean

@AppScope
class MainActivityModelView @Inject constructor(
    private val context: Context,
    @Named(HasSaveEventName)
    private val hasSaveEvent: HasSaveEvent,
    private val saveController: SaveController
) {
    companion object {
        const val HasSaveEventName = "hasSaveEvent"
    }

    val buttonsParameters: ButtonParameters = arrayOf(
        "load game" to this::loadGame,
        "new game" to this::startNewGame,
        "ranking" to this::openRanking,
        "settings" to this::openSettings,
    )

    fun isLoadGameAction(action: () -> Unit): Boolean =
        action == this::loadGame

    private fun openRanking() {
        startActivityHelper(RankingActivity::class.java)
    }

    private fun openSettings() {
        startActivityHelper(SettingsActivity::class.java)
    }

    private fun <T> startActivityHelper(x: Class<T>) {
        context.startActivity(
            Intent(context, x)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun loadGame() {
        startGame(true)
    }

    private fun startNewGame() {
        startGame(false)
    }

    private fun startGame(loadGame: Boolean) {
        val intent = Intent(context, GameActivity::class.java)
            .putExtra(GameActivity.LoadGame, loadGame)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

    }

    fun invalidate() {
        hasSaveEvent.onDataChanged(
            saveController.hasData(
                SaveTypes.SaveGameJson
            )
        )
    }
}