package com.surovtsev.cool_3d_minesweeper.models.game.interaction

import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.UpdatableOnOffSwitch

object GameControlsNames {
    const val RemoveMarkedBombs = "removeMarkedBombs"
    const val RemoveZeroBorders = "removeZeroBorders"
    const val MarkOnShortTap = "markOnShortTap"
}

typealias RemoveMarkedBombsControl = Updatable
typealias RemoveZeroBordersControl = Updatable
typealias MarkOnShortTapControl = UpdatableOnOffSwitch
