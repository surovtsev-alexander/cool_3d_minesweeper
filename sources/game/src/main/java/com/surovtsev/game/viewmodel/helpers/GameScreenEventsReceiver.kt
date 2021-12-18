package com.surovtsev.game.viewmodel.helpers

import android.content.Context
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.minesweeper.gamelogic.gameinteraction.GameEventsReceiver
import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.game.models.game.gamestatus.GameStatusHelper
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameScreenEventsReceiver @Inject constructor(
    private val context: Context,
    @Named(GameScreenEventsNames.ShowDialog)
    private val showDialog: ShowDialogEvent
): GameEventsReceiver {
    init {
        init()
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