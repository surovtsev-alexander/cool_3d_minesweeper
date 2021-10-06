package com.surovtsev.cool3dminesweeper.model_views.game_screen_view_model.helpers

import android.content.Context
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool3dminesweeper.models.game.game_status.GameStatusHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameScreenEventsReceiver @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named(GameScreenEventsNames.BombsLeft)
    private val bombsLeft: BombsLeftEvent,
    @Named(GameScreenEventsNames.ElapsedTime)
    private val elapsedTime: ElapsedTimeEvent,
    @Named(GameScreenEventsNames.ShowDialog)
    private val showDialog: ShowDialogEvent
): IGameEventsReceiver {
    init {
        init()
    }

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

    override fun gameStatusUpdated(
        newStatus: GameStatus,
        elapsed: Long
    ) {
        context.runOnUiThread {
            if (GameStatusHelper.isGameOver(newStatus)) {
                showDialog.onDataChanged(true)
            }
        }
    }
}