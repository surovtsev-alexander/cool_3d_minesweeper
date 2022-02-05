package com.surovtsev.utils.timers.async

import kotlinx.coroutines.flow.StateFlow

typealias TimeAfterDeviceStartupFlow = StateFlow<Long>

interface TimeAfterDeviceStartupFlowHolder {
    val timeAfterDeviceStartupFlow: TimeAfterDeviceStartupFlow
}