package com.surovtsev.utils.dagger.components.helpers

import com.surovtsev.utils.dagger.components.RestartableCoroutineScopeEntryPoint
import com.surovtsev.utils.dagger.components.SubscriptionsHolderEntryPoint

typealias SubscriptionsHolderComponentFactory =
            (restartableCoroutineScopeEntryPoint: RestartableCoroutineScopeEntryPoint, name: String) -> SubscriptionsHolderEntryPoint
