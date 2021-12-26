package com.surovtsev.utils.timers.fpscalculator

import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.common.DelayedStateFlowHolder

class DelayedFPSFlowHolder(
    timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
    updateInterval: Int = 300,
    fpsCalculator: FPSCalculator,
    subscriber: Subscriber,
) : DelayedStateFlowHolder<Float>(
    timeAfterDeviceStartupFlowHolder,
    updateInterval,
    fpsCalculator,
    0f,
    subscriber
)