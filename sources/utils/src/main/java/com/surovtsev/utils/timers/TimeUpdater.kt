package com.surovtsev.utils.timers

interface TimeUpdater {
    fun subscribe(
        x: Tickable
    )
}
