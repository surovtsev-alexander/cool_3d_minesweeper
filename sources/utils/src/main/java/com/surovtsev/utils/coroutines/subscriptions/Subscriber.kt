package com.surovtsev.utils.coroutines.subscriptions

interface Subscriber {
    fun addSubscription(
        subscription: Subscription
    )
}
