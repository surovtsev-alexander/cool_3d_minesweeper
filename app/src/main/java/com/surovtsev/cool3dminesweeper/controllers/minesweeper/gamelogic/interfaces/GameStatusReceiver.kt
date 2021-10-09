package com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.interfaces

import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatus
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatusHelper

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