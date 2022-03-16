package com.surovtsev.utils.coroutines.customcoroutinescope.subscription

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import kotlinx.coroutines.CoroutineScope


class SubscriptionsHolder(
    private val customCoroutineScope: CustomCoroutineScope
) {
    val coroutineScope = customCoroutineScope as CoroutineScope

    private val subscriptions: MutableList<Subscription> =
        emptyList<Subscription>().toMutableList()

    fun addSubscription(
        subscription: Subscription
    ) {
        subscriptions += subscription

        subscription.initSubscription(
            customCoroutineScope
        )
    }

    fun initSubscriptions(
        customCoroutineScope: CustomCoroutineScope
    ) {
        subscriptions.map {
            it.initSubscription(customCoroutineScope)
        }
    }
}
