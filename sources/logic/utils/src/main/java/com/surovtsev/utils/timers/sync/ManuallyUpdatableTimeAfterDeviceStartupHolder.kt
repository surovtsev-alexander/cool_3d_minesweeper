package com.surovtsev.utils.timers.sync

import android.os.SystemClock

class ManuallyUpdatableTimeAfterDeviceStartupHolder {
    var timeAfterDeviceStartup: Long = 0L
        private set

    fun tick() {
        timeAfterDeviceStartup = SystemClock.elapsedRealtime()
    }
}