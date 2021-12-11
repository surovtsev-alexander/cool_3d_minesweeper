package com.surovtsev.cool3dminesweeper.models.game.interaction

import com.surovtsev.core.utils.statehelpers.UpdatableImp
import com.surovtsev.core.utils.statehelpers.UpdatableOnOffSwitch

object GameControlsNames {
    const val RemoveMarkedBombs = "removeMarkedBombs"
    const val RemoveZeroBorders = "removeZeroBorders"
    const val MarkOnShortTap = "markOnShortTap"
}

typealias RemoveMarkedBombsControl = UpdatableImp
typealias RemoveZeroBordersControl = UpdatableImp
typealias MarkOnShortTapControl = UpdatableOnOffSwitch
