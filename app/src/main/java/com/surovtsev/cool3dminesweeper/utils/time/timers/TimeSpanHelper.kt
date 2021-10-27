package com.surovtsev.cool3dminesweeper.utils.time.timers

import android.os.SystemClock
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import javax.inject.Inject

@GameScope
class TimeSpanHelper @Inject constructor(): Tickable {

    var timeAfterDeviceStartup = 0L

    init {
        tick()
    }

    override fun tick() {
        timeAfterDeviceStartup = SystemClock.elapsedRealtime()
    }
}
