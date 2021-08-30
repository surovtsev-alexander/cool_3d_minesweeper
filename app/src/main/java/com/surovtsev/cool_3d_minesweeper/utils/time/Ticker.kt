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

    private var  elapsedTimeBeforePause = 0L
    private var onTime = currentTime()
    private var prev = onTime

    private fun currentTime() = clock.time

    fun tick() {
        val currTime = currentTime()

        if (currTime - prev >= interval) {
            update()
            prev = currTime
        }
    }

    fun getElapsed() = elapsedTimeBeforePause + currentTime() - onTime

    override fun turnOn() {
        switch.turnOn()

        onTime = currentTime()
        prev = onTime

        update()
    }

    override fun turnOff() {
        switch.turnOff()

        elapsedTimeBeforePause = getElapsed()
    }

    override fun isOn() = switch.isOn()
}