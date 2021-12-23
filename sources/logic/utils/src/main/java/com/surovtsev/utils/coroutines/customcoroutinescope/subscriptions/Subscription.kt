package com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope

interface Subscription {
    fun initSubscription(
        customCoroutineScope: CustomCoroutineScope
    )
}