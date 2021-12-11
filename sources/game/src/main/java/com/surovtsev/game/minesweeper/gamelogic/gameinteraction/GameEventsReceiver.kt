package com.surovtsev.game.minesweeper.gamelogic.gameinteraction

interface GameEventsReceiver: GameStatusReceiver {
    fun bombCountUpdated(newValue: Int)
    fun timeUpdated(newValue: Long)

    override fun init() {
        super.init()
        bombCountUpdated(0)
        timeUpdated(0L)
    }
}
