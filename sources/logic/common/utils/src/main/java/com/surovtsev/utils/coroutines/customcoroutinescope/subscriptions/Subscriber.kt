package com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions

import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolderWithName

interface Subscriber {
    fun addSubscriptionHolder(
        subscriptionsHolderWithName: SubscriptionsHolderWithName,
    )

    fun removeSubscriptionHolder(
        name: String
    )
}