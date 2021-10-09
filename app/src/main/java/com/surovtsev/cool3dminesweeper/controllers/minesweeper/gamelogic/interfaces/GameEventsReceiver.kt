package com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.interfaces

interface GameEventsReceiver: GameStatusReceiver {
    fun bombCountUpdated(newValue: Int)
    fun timeUpdated(newValue: Long)

    override fun init() {
        super.init()
        bombCountUpdated(0)
        timeUpdated(0L)
    }
}
