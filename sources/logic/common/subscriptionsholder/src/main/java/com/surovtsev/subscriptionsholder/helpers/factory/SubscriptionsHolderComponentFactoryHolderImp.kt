package com.surovtsev.subscriptionsholder.helpers.factory

import com.surovtsev.subscriptionsholder.dagger.DaggerSubscriptionsHolderComponent
import com.surovtsev.utils.dagger.components.helpers.SubscriptionsHolderComponentFactory
import com.surovtsev.utils.dagger.components.helpers.SubscriptionsHolderComponentFactoryHolder

object SubscriptionsHolderComponentFactoryHolderImp: SubscriptionsHolderComponentFactoryHolder {

    override val create: SubscriptionsHolderComponentFactory = { restartableCoroutineScopeEntryPoint, name ->
        DaggerSubscriptionsHolderComponent
            .builder()
            .restartableCoroutineScopeEntryPoint(restartableCoroutineScopeEntryPoint)
            .subscriptionsHolderName(name)
            .build()
    }

    override val createAndSubscribe: SubscriptionsHolderComponentFactory = { restartableCoroutineScopeEntryPoint, name ->
        create(restartableCoroutineScopeEntryPoint, name).also {
            restartableCoroutineScopeEntryPoint.subscriberImp.addSubscriptionHolder(
                it.subscriptionsHolderWithName
            )
        }
    }
}