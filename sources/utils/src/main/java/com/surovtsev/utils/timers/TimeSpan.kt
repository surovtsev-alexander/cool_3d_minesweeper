package com.surovtsev.utils.timers

import com.surovtsev.utils.statehelpers.Switch
import com.surovtsev.utils.statehelpers.SwitchImp
import com.surovtsev.utils.statehelpers.UpdatableImp


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

    init {
        flush()
    }

    private fun timeAfterDeviceStartup() = timeSpanHelper.timeAfterDeviceStartup

    fun flush() {
        turnOff()

        elapsedTimeBeforePause = 0L
        onTime = timeAfterDeviceStartup()
        prev = onTime
    }

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