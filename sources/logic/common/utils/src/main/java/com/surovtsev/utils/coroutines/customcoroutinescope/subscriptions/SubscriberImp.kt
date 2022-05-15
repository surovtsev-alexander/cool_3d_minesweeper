package com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions

import com.surovtsev.utils.coroutines.customcoroutinescope.BeforeStartAction
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolderWithName

class SubscriberImp(
    private val customCoroutineScope: CustomCoroutineScope
): Subscriber {
    private val subscriptionsHolderMap: MutableMap<String, SubscriptionsHolder> =
        mapOf<String, SubscriptionsHolder>().toMutableMap()

    override fun addSubscriptionHolder(
        subscriptionsHolderWithName: SubscriptionsHolderWithName,
    ) {
        removeSubscriptionHolder(
            subscriptionsHolderWithName.name
        )
        subscriptionsHolderMap += subscriptionsHolderWithName.toPair()

        initSubscriptionHolder(
            subscriptionsHolderWithName.subscriptionsHolder
        )
    }

    override fun removeSubscriptionHolder(
        name: String
    ) {
        subscriptionsHolderMap.remove(
            name
        )
    }

    fun restart(
        beforeStartAction: BeforeStartAction? = null
    ) {
        customCoroutineScope.restart(
            beforeStartAction
        )

        subscriptionsHolderMap.map { (_, subscriptionsHolder) ->
            initSubscriptionHolder(
                subscriptionsHolder
            )
        }
    }

    fun stop() {
        customCoroutineScope.turnOff()
    }

    private fun initSubscriptionHolder(
        subscriptionsHolder: SubscriptionsHolder
    ) {
        subscriptionsHolder.initSubscriptions(
            customCoroutineScope
        )
    }
}