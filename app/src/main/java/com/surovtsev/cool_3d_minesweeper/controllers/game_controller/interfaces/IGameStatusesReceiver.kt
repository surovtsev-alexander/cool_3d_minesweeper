package com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces

import com.surovtsev.cool_3d_minesweeper.models.game.GameStatus

interface IGameStatusesReceiver {
    fun gameStatusUpdated(newStatus: GameStatus)
}