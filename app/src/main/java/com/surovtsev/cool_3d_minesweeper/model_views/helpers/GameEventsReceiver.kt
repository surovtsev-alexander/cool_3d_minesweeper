package com.surovtsev.cool_3d_minesweeper.model_views.helpers

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.dagger.GameScope
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameEventsReceiver @Inject constructor(
    private val context: Context,
    @Named(GameViewEventNames.BombsLeft)
    private val bombsLeft: MyLiveData<Int>,
    @Named(GameViewEventNames.ElapsedTime)
    private val elapsedTime: MyLiveData<Long>,
    @Named(GameViewEventNames.ShowDialog)
    private val showDialog: MyLiveData<Boolean>
): IGameEventsReceiver {

    override fun bombCountUpdated(newValue: Int) {
        context.runOnUiThread {
            bombsLeft.onDataChanged(newValue)
        }
    }

    override fun timeUpdated(newValue: Long) {
        context.runOnUiThread {
            elapsedTime.onDataChanged(newValue)
        }
    }

    override fun gameStatusUpdated(newStatus: GameStatus) {
        context.runOnUiThread {
            if (GameStatusHelper.isGameOver(newStatus)) {
                showDialog.onDataChanged(true)
            }
        }
    }
}