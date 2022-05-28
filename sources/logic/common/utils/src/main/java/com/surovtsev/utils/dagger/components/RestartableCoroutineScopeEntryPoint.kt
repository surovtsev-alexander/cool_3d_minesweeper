package com.surovtsev.utils.dagger.components

import com.surovtsev.utils.coroutines.customcoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.SubscriberImp

interface RestartableCoroutineScopeEntryPoint {
    val restartableCoroutineScope: RestartableCoroutineScope

    val subscriberImp: SubscriberImp
    val subscriber: Subscriber
}