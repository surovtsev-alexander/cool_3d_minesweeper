package com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder

data class SubscriptionsHolderWithName(
    val subscriptionsHolder: SubscriptionsHolder,
    val name: String,
) {
    fun toPair(
    ): Pair<String, SubscriptionsHolder> {
        return name to subscriptionsHolder
    }
}
