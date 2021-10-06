package com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers

import android.content.Context
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.interfaces.IGameEventsReceiver
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatus
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatusHelper
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