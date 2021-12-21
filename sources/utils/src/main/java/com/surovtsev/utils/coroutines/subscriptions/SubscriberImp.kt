package com.surovtsev.utils.coroutines.subscriptions

import com.surovtsev.utils.coroutines.CustomCoroutineScope

class SubscriberImp(
    private val customCoroutineScope: CustomCoroutineScope
): Subscriber {
    private val subscriptions: MutableList<Subscription> =
        emptyList<Subscription>().toMutableList()

    override fun addSubscription(
        subscription: Subscription
    ) {
        subscriptions += subscription

        subscription.initSubscription(customCoroutineScope)
    }

    fun restart() {
        customCoroutineScope.restart()
        initSubscriptions()
    }

    fun onStop() {
        customCoroutineScope.turnOff()
    }

    private fun initSubscriptions() {
        subscriptions.map {
            it.initSubscription(customCoroutineScope)
        }
    }
}