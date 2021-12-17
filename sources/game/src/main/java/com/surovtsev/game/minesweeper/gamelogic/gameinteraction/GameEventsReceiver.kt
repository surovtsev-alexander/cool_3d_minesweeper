package com.surovtsev.game.minesweeper.gamelogic.gameinteraction

interface GameEventsReceiver: GameStatusReceiver {
    fun timeUpdated(newValue: Long)

    override fun init() {
        super.init()
        timeUpdated(0L)
    }
}
