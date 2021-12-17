package com.surovtsev.game.minesweeper.gamelogic.gameinteraction

interface GameEventsReceiver: GameStatusReceiver {
    override fun init() {
        super.init()
    }
}
