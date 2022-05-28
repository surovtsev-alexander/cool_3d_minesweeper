package com.surovtsev.utils.dagger.components

import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriber.Subscriber
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriber.SubscriberImp

interface RestartableCoroutineScopeEntryPoint {
    val restartableCoroutineScope: RestartableCoroutineScope

    val subscriberImp: SubscriberImp
    val subscriber: Subscriber
}