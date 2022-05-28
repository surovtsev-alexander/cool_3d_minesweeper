package com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriber

import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolderWithName

interface Subscriber {
    fun addSubscriptionHolder(
        subscriptionsHolderWithName: SubscriptionsHolderWithName,
    )

    fun removeSubscriptionHolder(
        name: String
    )
}