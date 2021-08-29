package com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces

import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus

interface IGameStatusesReceiver {
    fun gameStatusUpdated(newStatus: GameStatus)
    fun bombCountUpdated()
}