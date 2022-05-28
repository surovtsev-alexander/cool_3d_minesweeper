package com.surovtsev.utils.timers.common

import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias DelayedStateFlow<T> = StateFlow<T>

open class DelayedStateFlowHolder<T>(
    private val timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
    private val updateInterval: Int,
    private val stateHolder: StateHolder<T>,
    defaultValue: T,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription
{
    private val _flow = MutableStateFlow<T>(defaultValue)
    val flow: DelayedStateFlow<T> = _flow.asStateFlow()

    private var prevTime = 0L

    init {
        subscriptionsHolder
            .addSubscription(this)
    }

    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        restartableCoroutineScope.launch {
            timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.collectLatest {
                tick(it)
            }
        }
    }

    private fun tick(
        currTime: Long
    ) {
        val diff = currTime - prevTime

        if (diff >= updateInterval) {
            prevTime = currTime

            _flow.value = stateHolder.state
        }
    }
}