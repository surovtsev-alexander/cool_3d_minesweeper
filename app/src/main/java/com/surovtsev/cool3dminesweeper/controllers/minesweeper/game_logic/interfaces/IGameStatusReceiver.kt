package com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.interfaces

import com.surovtsev.cool3dminesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool3dminesweeper.models.game.game_status.GameStatusHelper

interface IGameStatusReceiver {
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