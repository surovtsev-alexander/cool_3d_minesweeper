package com.surovtsev.utils.coroutines.subscriptions

import com.surovtsev.utils.coroutines.CustomCoroutineScope

interface Subscription {
    fun initSubscription(
        customCoroutineScope: CustomCoroutineScope
    )
}