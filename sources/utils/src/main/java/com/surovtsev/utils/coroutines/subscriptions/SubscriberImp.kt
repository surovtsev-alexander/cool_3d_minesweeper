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
    }

    fun onStart() {
        customCoroutineScope.onStart()
        initSubscriptions()
    }

    fun onStop() {
        customCoroutineScope.onStop()
    }

    private fun initSubscriptions() {
        subscriptions.map {
            it.initSubscription(customCoroutineScope)
        }
    }
}