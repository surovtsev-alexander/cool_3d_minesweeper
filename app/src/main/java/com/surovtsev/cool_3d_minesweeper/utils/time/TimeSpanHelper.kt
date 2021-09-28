package com.surovtsev.cool_3d_minesweeper.utils.time

import android.os.SystemClock
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameControllerScope
import javax.inject.Inject

@GameControllerScope
class TimeSpanHelper @Inject constructor(): INeedToBeUpdated {

    var timeAfterDeviceStartup = 0L

    init {
        tick()
    }

    override fun tick() {
        timeAfterDeviceStartup = SystemClock.elapsedRealtime()
    }
}
