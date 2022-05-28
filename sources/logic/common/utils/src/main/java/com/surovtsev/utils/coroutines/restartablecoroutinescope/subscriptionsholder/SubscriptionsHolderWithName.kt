package com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder

data class SubscriptionsHolderWithName(
    val subscriptionsHolder: SubscriptionsHolder,
    val name: String,
) {
    fun toPair(
    ): Pair<String, SubscriptionsHolder> {
        return name to subscriptionsHolder
    }
}
