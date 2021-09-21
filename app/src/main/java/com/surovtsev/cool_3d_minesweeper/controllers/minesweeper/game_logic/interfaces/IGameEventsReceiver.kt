package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces

import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper

interface IGameEventsReceiver: IGameStatusReceiver {
    fun bombCountUpdated(newValue: Int)
    fun timeUpdated(newValue: Long)

    override fun init() {
        super.init()
        bombCountUpdated(0)
        timeUpdated(0L)
    }
}
