package com.surovtsev.cool_3d_minesweeper.utils.time

import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.ISwitch
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Switch

class Ticker(
    private val interval: Long,
    private val clock: CustomRealtime,
):
    Updatable(),
    ISwitch
{
    private val switch: Switch = Switch()

    private var startTime = currentTime()
    private var prev = startTime

    private fun currentTime() = clock.time

    fun tick() {
        val currTime = currentTime()

        if (currTime - prev >= interval) {
            update()
            prev = currTime
        }
    }

    fun getElapsed() = currentTime() - startTime

    override fun turnOn() {
        switch.turnOn()

        startTime = currentTime()
        prev = startTime

        update()
    }

    override fun turnOff() = switch.turnOff()

    override fun isOn() = switch.isOn()
}