package com.surovtsev.cool_3d_minesweeper.utils

import com.surovtsev.cool_3d_minesweeper.utils.CustomClock
import com.surovtsev.cool_3d_minesweeper.utils.DelayedRelease

class Ticker(val interval: Long, val clock: CustomClock): DelayedRelease(), ISwitch by Switch() {
    private var startTime = currentTime()
        private set
    private var prev = startTime

    private fun currentTime() = clock.time

    fun tick() {
        val currTime = currentTime()

        if (currTime - prev >= interval) {
            update()
            prev = currTime
        }
    }

    fun start() {
        startTime = currentTime()
        prev = startTime
        turnOn()
        update()
    }

    fun getElapsed() = currentTime() - startTime
}