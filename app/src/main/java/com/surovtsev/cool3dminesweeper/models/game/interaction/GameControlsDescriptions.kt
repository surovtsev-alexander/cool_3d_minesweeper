package com.surovtsev.cool3dminesweeper.models.game.interaction

import com.surovtsev.cool3dminesweeper.utils.statehelpers.Updatable
import com.surovtsev.cool3dminesweeper.utils.statehelpers.UpdatableOnOffSwitch

object GameControlsNames {
    const val RemoveMarkedBombs = "removeMarkedBombs"
    const val RemoveZeroBorders = "removeZeroBorders"
    const val MarkOnShortTap = "markOnShortTap"
}

typealias RemoveMarkedBombsControl = Updatable
typealias RemoveZeroBordersControl = Updatable
typealias MarkOnShortTapControl = UpdatableOnOffSwitch
