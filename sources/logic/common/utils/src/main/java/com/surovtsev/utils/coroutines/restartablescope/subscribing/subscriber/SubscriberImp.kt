package com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriber

import com.surovtsev.utils.coroutines.restartablescope.BeforeStartAction
import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolderWithName
import com.surovtsev.utils.statehelpers.IsOn

class SubscriberImp(
    private val restartableCoroutineScope: RestartableCoroutineScope
):
    Subscriber,
    IsOn by restartableCoroutineScope
{

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
        restartableCoroutineScope.restart(
            beforeStartAction
        )

        subscriptionsHolderMap.map { (_, subscriptionsHolder) ->
            initSubscriptionHolder(
                subscriptionsHolder
            )
        }
    }

    fun stop() {
        restartableCoroutineScope.turnOff()
    }

    private fun initSubscriptionHolder(
        subscriptionsHolder: SubscriptionsHolder
    ) {
        subscriptionsHolder.initSubscriptions(
            restartableCoroutineScope
        )
    }
}