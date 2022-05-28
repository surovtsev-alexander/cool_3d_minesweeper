package com.surovtsev.utils.dagger.components

import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolderWithName

interface SubscriptionsHolderEntryPoint {
    val subscriptionsHolder: SubscriptionsHolder
    val subscriptionsHolderWithName: SubscriptionsHolderWithName
}