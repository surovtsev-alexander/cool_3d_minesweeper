package com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.interfaces

interface IGameEventsReceiver: IGameStatusReceiver {
    fun bombCountUpdated(newValue: Int)
    fun timeUpdated(newValue: Long)

    override fun init() {
        super.init()
        bombCountUpdated(0)
        timeUpdated(0L)
    }
}
