package com.surovtsev.utils.coroutines.customcoroutinescope.subscription

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope


class SubscriptionsHolder(
    private val customCoroutineScope: CustomCoroutineScope
) {
    companion object {
        const val NAME = "SUBSCRIPTIONS_HOLDER_NAME"
    }

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
