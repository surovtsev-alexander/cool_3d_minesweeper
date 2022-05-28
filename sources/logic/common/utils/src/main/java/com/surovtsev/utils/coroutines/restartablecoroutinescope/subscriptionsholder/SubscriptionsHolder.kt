package com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder

import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscription.Subscription
import kotlinx.coroutines.CoroutineScope


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
