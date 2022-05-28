package com.surovtsev.utils.coroutines.customsupervisorscope

import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription.Subscription
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CustomSupervisedScope(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
): CoroutineScope, Subscription {

    private var job: CompletableJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + job

    override fun initSubscription(
        restartableCoroutineScope: RestartableCoroutineScope
    ) {
        job.cancel()
        job = restartableCoroutineScope.createSupervisorJob()
    }
}