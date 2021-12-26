package com.surovtsev.utils.timers.common

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscription
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
    subscriber: Subscriber,
): Subscription
{
    private val _flow = MutableStateFlow<T>(defaultValue)
    val flow: DelayedStateFlow<T> = _flow.asStateFlow()

    private var prevTime = 0L

    init {
        subscriber
            .addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
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