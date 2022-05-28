package com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriber

import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolderWithName

interface Subscriber {
    fun addSubscriptionHolder(
        subscriptionsHolderWithName: SubscriptionsHolderWithName,
    )

    fun removeSubscriptionHolder(
        name: String
    )
}