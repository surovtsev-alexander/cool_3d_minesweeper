package com.surovtsev.utils.dagger.components.helpers

interface SubscriptionsHolderComponentFactoryHolder {
    val create: SubscriptionsHolderComponentFactory
    val createAndSubscribe: SubscriptionsHolderComponentFactory
}
