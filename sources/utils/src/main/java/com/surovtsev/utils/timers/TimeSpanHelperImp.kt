package com.surovtsev.utils.timers

import android.os.SystemClock

class TimeSpanHelperImp: TimeSpanHelper {

    override var timeAfterDeviceStartup: Long = 0L
        private set


    private val subscribers = emptyList<Tickable>().toMutableList()

    init {
        tick()
    }

    fun forgetSubscribers() {
        subscribers.clear()
    }

    override fun tick() {
        timeAfterDeviceStartup = SystemClock.elapsedRealtime()

        subscribers.forEach {
            it.tick()
        }
    }

    override fun subscribe(
        x: Tickable
    ) {
        subscribers.add(x)
    }
}
