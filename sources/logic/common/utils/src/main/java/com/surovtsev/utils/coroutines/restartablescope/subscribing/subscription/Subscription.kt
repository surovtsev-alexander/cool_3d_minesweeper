package com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription

import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope

interface Subscription {
    fun initSubscription(
        restartableCoroutineScope: RestartableCoroutineScope
    )
}