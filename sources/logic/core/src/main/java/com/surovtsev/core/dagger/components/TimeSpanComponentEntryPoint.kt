package com.surovtsev.core.dagger.components

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.SubscriberImp
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.timers.TimeSpanHelper
import com.surovtsev.utils.timers.TimeSpanHelperImp

interface TimeSpanComponentEntryPoint {
    val timeSpan: TimeSpan
    val timeSpanHelperImp: TimeSpanHelperImp
    val timeSpanHelper: TimeSpanHelper
    val customCoroutineScope: CustomCoroutineScope

    val subscriberImp: SubscriberImp
    val subscriber: Subscriber
}