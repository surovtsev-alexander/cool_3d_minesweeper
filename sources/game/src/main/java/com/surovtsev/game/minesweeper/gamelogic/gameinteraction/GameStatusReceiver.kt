package com.surovtsev.game.minesweeper.gamelogic.gameinteraction

import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.game.models.game.gamestatus.GameStatusHelper


interface GameStatusReceiver {
    fun gameStatusUpdated(
        newStatus: GameStatus,
        elapsed: Long
    )

    fun init() {
        gameStatusUpdated(
            GameStatusHelper.initStatus,
            0
        )
    }
}