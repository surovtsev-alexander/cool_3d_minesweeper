package com.surovtsev.cool3dminesweeper.utils.time.timers

interface TimeUpdater {
    fun subscribe(
        x: Tickable
    )
}
