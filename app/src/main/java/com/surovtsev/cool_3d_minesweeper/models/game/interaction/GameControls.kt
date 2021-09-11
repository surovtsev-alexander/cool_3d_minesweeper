package com.surovtsev.cool_3d_minesweeper.models.game.interaction

import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.UpdatableOnOffSwitch

class GameControls {
    val removeBombs = Updatable(false)
    val removeZeroBorders = Updatable(false)
    val markOnShortTap = UpdatableOnOffSwitch()
}
