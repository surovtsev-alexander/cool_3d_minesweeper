package com.surovtsev.cool_3d_minesweeper.utils.time

import android.os.SystemClock

class TimeSpanHelper: INeedToBeUpdated {

    var timeAfterDeviceStartup = 0L

    init {
        tick()
    }

    override fun tick() {
        timeAfterDeviceStartup = SystemClock.elapsedRealtime()
    }
}
