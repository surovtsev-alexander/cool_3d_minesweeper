package com.surovtsev.cool3dminesweeper.utils.time

import com.surovtsev.cool3dminesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool3dminesweeper.utils.state_helpers.ISwitch
import com.surovtsev.cool3dminesweeper.utils.state_helpers.Switch

class TimeSpan(
    private val interval: Long,
    private val timeSpanHelper: TimeSpanHelper,
):
    Updatable(),
    ISwitch
{
    private val switch: Switch = Switch()

    private var  elapsedTimeBeforePause = 0L
    private var onTime = timeAfterDeviceStartup()
    private var prev = onTime

    private fun timeAfterDeviceStartup() = timeSpanHelper.timeAfterDeviceStartup

    fun tick() {
        val currTime = timeAfterDeviceStartup()

        if (currTime - prev >= interval) {
            update()
            prev = currTime
        }
    }

    fun getElapsed() = elapsedTimeBeforePause + (if (isOn()){ timeAfterDeviceStartup() - onTime } else 0L)

    fun setElapsed(elapsed: Long) {
        elapsedTimeBeforePause = elapsed
    }

    override fun turnOn() {
        switch.turnOn()

        onTime = timeAfterDeviceStartup()
        prev = onTime

        update()
    }

    override fun turnOff() {
        elapsedTimeBeforePause = getElapsed()

        switch.turnOff()
    }

    override fun isOn() = switch.isOn()
}