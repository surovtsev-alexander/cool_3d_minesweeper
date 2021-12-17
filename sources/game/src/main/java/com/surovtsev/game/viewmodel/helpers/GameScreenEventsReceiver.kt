package com.surovtsev.game.viewmodel.helpers

import android.content.Context
import com.surovtsev.game.minesweeper.gamelogic.gameinteraction.GameEventsReceiver
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.game.models.game.gamestatus.GameStatusHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameScreenEventsReceiver @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named(GameScreenEventsNames.ElapsedTime)
    private val elapsedTime: ElapsedTimeEvent,
    @Named(GameScreenEventsNames.ShowDialog)
    private val showDialog: ShowDialogEvent
): GameEventsReceiver {
    init {
        init()
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