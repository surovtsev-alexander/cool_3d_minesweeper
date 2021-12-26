package com.surovtsev.utils.timers.async

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow

class ManuallyUpdatableTimeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder {
    private val _timeAfterDeviceStartupFlow = MutableStateFlow(0L)

    override val timeAfterDeviceStartupFlow: TimeAfterDeviceStartupFlow = _timeAfterDeviceStartupFlow

    init {
        tick()
    }

    fun tick() {
        _timeAfterDeviceStartupFlow.value = SystemClock.elapsedRealtime()
    }
}
