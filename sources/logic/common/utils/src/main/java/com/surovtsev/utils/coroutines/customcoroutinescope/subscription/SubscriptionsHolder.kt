package com.surovtsev.utils.coroutines.customcoroutinescope.subscription

import com.surovtsev.utils.coroutines.customcoroutinescope.RestartableCoroutineScope
import kotlinx.coroutines.CoroutineScope


class SubscriptionsHolder(
    private val restartableCoroutineScope: RestartableCoroutineScope
) {
    val coroutineScope = restartableCoroutineScope as CoroutineScope

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
