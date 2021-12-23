package com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions

interface Subscriber {
    fun addSubscription(
        subscription: Subscription
    )
}
