package com.surovtsev.gamelogic.models.game.interaction


class GameControlsImp(
    var removeFlaggedCells: Boolean = false,
    var removeOpenedSlices: Boolean = false,
    override var flagging: Boolean = false,
): GameControls {
    fun flush() {
        removeFlaggedCells = false
        removeOpenedSlices = false
    }
}
