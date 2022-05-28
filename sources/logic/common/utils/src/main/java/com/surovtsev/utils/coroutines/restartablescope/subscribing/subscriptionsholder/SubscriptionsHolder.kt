package com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder

import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription.Subscription


class SubscriptionsHolder(
    private val restartableCoroutineScope: RestartableCoroutineScope
) {
    private val subscriptions: MutableList<Subscription> =
        emptyList<Subscription>().toMutableList()

    fun addSubscription(
        subscription: Subscription
    ) {
        subscriptions += subscription

        subscription.initSubscription(
            restartableCoroutineScope
        )
    }

    fun initSubscriptions(
        restartableCoroutineScope: RestartableCoroutineScope
    ) {
        subscriptions.map {
            it.initSubscription(restartableCoroutineScope)
        }
    }
}
