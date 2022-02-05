package com.surovtsev.utils.coroutines.customcoroutinescope.subscription

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope

interface Subscription {
    fun initSubscription(
        customCoroutineScope: CustomCoroutineScope
    )
}