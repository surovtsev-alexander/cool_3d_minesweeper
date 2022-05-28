package com.surovtsev.utils.dagger.components

import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolderWithName

interface SubscriptionsHolderEntryPoint {
    val subscriptionsHolder: SubscriptionsHolder
    val subscriptionsHolderWithName: SubscriptionsHolderWithName
}