package com.surovtsev.utils.timers

import kotlinx.coroutines.flow.StateFlow

typealias TimeAfterDeviceStartupFlow = StateFlow<Long>

interface TimeSpanHelper {
    val timeAfterDeviceStartupFlow: TimeAfterDeviceStartupFlow
}