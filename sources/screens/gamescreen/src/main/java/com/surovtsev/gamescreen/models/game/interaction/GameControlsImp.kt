package com.surovtsev.gamescreen.models.game.interaction


class GameControlsImp(
    var removeFlaggedCells: Boolean = false,
    var removeOpenedBorders: Boolean = false,
    override var flagging: Boolean = false,
): GameControls {
    fun flush() {
        removeFlaggedCells = false
        removeOpenedBorders = false
    }
}
