package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces

import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper

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