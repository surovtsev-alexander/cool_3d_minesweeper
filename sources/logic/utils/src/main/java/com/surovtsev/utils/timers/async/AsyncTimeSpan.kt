package com.surovtsev.utils.timers.async

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.statehelpers.Switch
import com.surovtsev.utils.statehelpers.SwitchImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias TimeSpanFlow = StateFlow<Long>

class AsyncTimeSpan(
    private val delayInterval: Long,
    private val timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
    subscriptionsHolder: SubscriptionsHolder,
):
    Subscription, Switch
{
    private val _timeSpanFlow = MutableStateFlow(0L)
    val timeSpanFlow: TimeSpanFlow = _timeSpanFlow.asStateFlow()

    private val switch: SwitchImp = SwitchImp()

    private var  elapsedTimeBeforePause = 0L
    private var onTime: Long = timeAfterDeviceStartup()
    private var prev = onTime

    init {
        flush()
        subscriptionsHolder
            .addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.collectLatest {
                if (isOn()) {
                    tick(it)
                }
            }
        }
    }

    private fun timeAfterDeviceStartup() = timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.value

    fun flush() {
        turnOff()

        elapsedTimeBeforePause = 0L
        onTime = timeAfterDeviceStartup()
        prev = onTime
        _timeSpanFlow.value = getElapsed()
    }

    private fun tick(currTime: Long) {
        if (currTime - prev >= delayInterval) {
            _timeSpanFlow.value = getElapsed()
            prev = currTime
        }
    }

    fun getElapsed() = elapsedTimeBeforePause + (if (isOn()){ timeAfterDeviceStartup() - onTime } else 0L)

    fun setElapsed(elapsed: Long) {
        elapsedTimeBeforePause = elapsed
    }

    override fun turnOn() {
        switch.turnOn()

        onTime = timeAfterDeviceStartup()
        prev = onTime

        _timeSpanFlow.value = getElapsed()
    }

    override fun turnOff() {
        elapsedTimeBeforePause = getElapsed()

        switch.turnOff()
    }

    override fun isOn() = switch.isOn()
}