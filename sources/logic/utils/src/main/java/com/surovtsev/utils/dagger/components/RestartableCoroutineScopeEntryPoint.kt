package com.surovtsev.utils.dagger.components

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.SubscriberImp

interface RestartableCoroutineScopeEntryPoint {
    val customCoroutineScope: CustomCoroutineScope

    val subscriberImp: SubscriberImp
    val subscriber: Subscriber
}