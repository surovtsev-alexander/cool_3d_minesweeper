package com.surovtsev.utils.timers

import com.surovtsev.utils.statehelpers.Switch
import com.surovtsev.utils.statehelpers.SwitchImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

typealias TimeSpanFlowData = MutableStateFlow<Long>
typealias TimeSpanFlow = StateFlow<Long>


class TimeSpan(
    private val interval: Long,
    private val timeSpanHelper: TimeSpanHelperImp,
    private val timeSpanFlowData: TimeSpanFlowData,
):
    TimeUpdater,
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
        timeSpanFlowData.value = getElapsed()
    }

    fun tick() {
        val currTime = timeAfterDeviceStartup()

        if (currTime - prev >= interval) {
            timeSpanFlowData.value = getElapsed()
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

        timeSpanFlowData.value = getElapsed()
    }

    override fun turnOff() {
        elapsedTimeBeforePause = getElapsed()

        switch.turnOff()
    }

    override fun isOn() = switch.isOn()
}