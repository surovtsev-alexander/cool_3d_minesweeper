package com.surovtsev.cool3dminesweeper.utils.time.timers

import android.os.SystemClock
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import javax.inject.Inject

@GameScope
class TimeSpanHelper @Inject constructor(): Tickable, TimeUpdater {

    var timeAfterDeviceStartup = 0L

    private val subscribers = emptyList<Tickable>().toMutableList()

    init {
        tick()
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
