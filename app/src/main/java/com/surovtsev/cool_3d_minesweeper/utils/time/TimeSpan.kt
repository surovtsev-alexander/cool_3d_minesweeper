package com.surovtsev.cool_3d_minesweeper.utils.time

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.ISwitch
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Switch

class TimeSpan(
    private val interval: Long,
    private val timeSpanHelper: TimeSpanHelper,
):
    Updatable(),
    ISwitch
{
    private val switch: Switch = Switch()

    var  elapsedTimeBeforePause = 0L
    var onTime = timeAfterDeviceStartup()
    var prev = onTime

    fun timeAfterDeviceStartup() = timeSpanHelper.timeAfterDeviceStartup

    fun tick() {
        val currTime = timeAfterDeviceStartup()

        if (currTime - prev >= interval) {
            update()
            prev = currTime
        }
    }

    fun getElapsed() = elapsedTimeBeforePause + timeAfterDeviceStartup() - onTime

    override fun turnOn() {
        switch.turnOn()

        onTime = timeAfterDeviceStartup()
        prev = onTime

        update()
    }

    override fun turnOff() {
        switch.turnOff()

        elapsedTimeBeforePause = getElapsed()
    }

    override fun isOn() = switch.isOn()
}