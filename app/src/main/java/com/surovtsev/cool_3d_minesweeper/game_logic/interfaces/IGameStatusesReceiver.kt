package com.surovtsev.cool_3d_minesweeper.game_logic.interfaces

import com.surovtsev.cool_3d_minesweeper.game_logic.data.GameStatus

interface IGameStatusesReceiver {
    fun gameStatusUpdated(newStatus: GameStatus)
}