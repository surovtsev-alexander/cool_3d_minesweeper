package com.surovtsev.utils.dagger.components

import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolderWithName

interface SubscriptionsHolderEntryPoint {
    val subscriptionsHolder: SubscriptionsHolder
    val subscriptionsHolderWithName: SubscriptionsHolderWithName
}