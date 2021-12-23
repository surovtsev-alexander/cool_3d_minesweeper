package com.surovtsev.gamescreen.models.game.interaction


class GameControlsImp(
    var removeFlaggedCells: Boolean = false,
    var removeZeroBorders: Boolean = false,
    override var flagging: Boolean = false,
): GameControls {
    fun toggleFlagging() {
        flagging = !flagging
    }

    fun flush() {
        removeFlaggedCells = false
        removeZeroBorders = false
    }
}
