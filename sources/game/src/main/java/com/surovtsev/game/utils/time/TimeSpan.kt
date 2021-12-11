package com.surovtsev.game.utils.time

import com.surovtsev.core.utils.statehelpers.Switch
import com.surovtsev.core.utils.statehelpers.SwitchImp
import com.surovtsev.core.utils.statehelpers.UpdatableImp
import com.surovtsev.utils.timers.Tickable
import com.surovtsev.utils.timers.TimeUpdater


class TimeSpan(
    private val interval: Long,
    private val timeSpanHelper: TimeSpanHelperImp,
):
    TimeUpdater,
    UpdatableImp(),
    Switch
{
    override fun subscribe(x: Tickable) {
        timeSpanHelper.subscribe(x)
    }

    private val switch: SwitchImp = SwitchImp()

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