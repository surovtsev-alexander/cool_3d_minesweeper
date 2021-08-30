package com.surovtsev.cool_3d_minesweeper.models.game.control

import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable

class GameControls {
    val removeBombs = Updatable(false)
    val removeBorderZeros = Updatable(false)
}
