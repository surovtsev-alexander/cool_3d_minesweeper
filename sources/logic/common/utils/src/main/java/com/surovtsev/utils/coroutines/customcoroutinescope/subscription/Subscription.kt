package com.surovtsev.utils.coroutines.customcoroutinescope.subscription

import com.surovtsev.utils.coroutines.customcoroutinescope.RestartableCoroutineScope

interface Subscription {
    fun initSubscription(
        restartableCoroutineScope: RestartableCoroutineScope
    )
}