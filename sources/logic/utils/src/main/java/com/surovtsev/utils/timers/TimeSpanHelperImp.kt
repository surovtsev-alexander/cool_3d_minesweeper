package com.surovtsev.utils.timers

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow

class TimeSpanHelperImp: TimeSpanHelper {
    private val _timeAfterDeviceStartupFlow = MutableStateFlow(0L)

    override val timeAfterDeviceStartupFlow: TimeAfterDeviceStartupFlow = _timeAfterDeviceStartupFlow

    init {
        tick()
    }

    fun tick() {
        _timeAfterDeviceStartupFlow.value = SystemClock.elapsedRealtime()
    }
}
