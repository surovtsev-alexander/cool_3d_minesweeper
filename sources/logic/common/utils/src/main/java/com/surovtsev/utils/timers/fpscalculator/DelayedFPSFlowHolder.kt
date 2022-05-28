package com.surovtsev.utils.timers.fpscalculator

import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.common.DelayedStateFlowHolder

class DelayedFPSFlowHolder(
    timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
    updateInterval: Int = 300,
    fpsCalculator: FPSCalculator,
    subscriptionsHolder: SubscriptionsHolder,
) : DelayedStateFlowHolder<Float>(
    timeAfterDeviceStartupFlowHolder,
    updateInterval,
    fpsCalculator,
    0f,
    subscriptionsHolder,
)