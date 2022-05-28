package com.surovtsev.utils.dagger.components

import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriber.Subscriber
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriber.SubscriberImp

interface RestartableCoroutineScopeEntryPoint {
    val restartableCoroutineScope: RestartableCoroutineScope

    val subscriberImp: SubscriberImp
    val subscriber: Subscriber
}