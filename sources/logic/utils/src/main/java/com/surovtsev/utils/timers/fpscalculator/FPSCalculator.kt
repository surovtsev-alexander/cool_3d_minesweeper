package com.surovtsev.utils.timers.fpscalculator

import com.surovtsev.utils.math.FloatingAverage
import com.surovtsev.utils.math.MyMath
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.common.StateHolder


class FPSCalculator(
    private val timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
    private val floatingAverage: FloatingAverage,
): StateHolder<Float> {
    private var lastStoredTime: Long = 0L

    override var state: Float = 0f
        private set

    fun onNextFrame() {
        val currTime = timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.value

        val timeDiff = currTime - lastStoredTime
        lastStoredTime = currTime


        val smoothedValue  = floatingAverage.next(timeDiff)

        state = if (MyMath.isZero(smoothedValue)) 0f else 1000f / smoothedValue
    }
}