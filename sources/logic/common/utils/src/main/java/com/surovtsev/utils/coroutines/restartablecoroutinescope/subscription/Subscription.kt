package com.surovtsev.utils.coroutines.restartablecoroutinescope.subscription

import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope

interface Subscription {
    fun initSubscription(
        restartableCoroutineScope: RestartableCoroutineScope
    )
}