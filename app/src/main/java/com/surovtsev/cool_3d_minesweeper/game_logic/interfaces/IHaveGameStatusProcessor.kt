package com.surovtsev.cool_3d_minesweeper.game_logic.interfaces

import com.surovtsev.cool_3d_minesweeper.game_logic.GameTouchHandler

interface IHaveGameStatusProcessor {
    fun gameStatusUpdated(s: GameTouchHandler.GameState)
}