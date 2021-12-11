package com.surovtsev.core.timers

interface TimeUpdater {
    fun subscribe(
        x: Tickable
    )
}
