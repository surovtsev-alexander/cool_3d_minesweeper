package com.surovtsev.core.dagger.components

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.SubscriberImp
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder

interface TimeSpanComponentEntryPoint {
    val asyncTimeSpan: AsyncTimeSpan
    val manuallyUpdatableTimeAfterDeviceStartupFlowHolder: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
    val timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder
    val customCoroutineScope: CustomCoroutineScope

    val subscriberImp: SubscriberImp
    val subscriber: Subscriber
}