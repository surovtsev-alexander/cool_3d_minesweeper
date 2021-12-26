package com.surovtsev.utils.timers.async

import com.surovtsev.utils.timers.sync.ManuallyUpdatableTimeAfterDeviceStartupHolder
import kotlinx.coroutines.flow.MutableStateFlow

class ManuallyUpdatableTimeAfterDeviceStartupFlowHolder(
    private val manuallyUpdatableTimeAfterDeviceStartupHolder: ManuallyUpdatableTimeAfterDeviceStartupHolder,
): TimeAfterDeviceStartupFlowHolder {
    private val _timeAfterDeviceStartupFlow = MutableStateFlow(0L)

    override val timeAfterDeviceStartupFlow: TimeAfterDeviceStartupFlow = _timeAfterDeviceStartupFlow

    init {
        tick()
    }

    fun tick() {
        manuallyUpdatableTimeAfterDeviceStartupHolder.let {
            it.tick()
            _timeAfterDeviceStartupFlow.value = it.timeAfterDeviceStartup
        }
    }
}
