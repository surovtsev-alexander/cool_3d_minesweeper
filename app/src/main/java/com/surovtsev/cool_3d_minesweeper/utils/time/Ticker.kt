package com.surovtsev.cool_3d_minesweeper.utils.time

import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.ISwitch
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Switch

class Ticker(val interval: Long, val clock: CustomClock): Updatable(), ISwitch by Switch() {
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