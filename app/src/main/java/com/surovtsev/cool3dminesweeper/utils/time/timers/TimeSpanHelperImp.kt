package com.surovtsev.cool3dminesweeper.utils.time.timers

import android.os.SystemClock
import com.surovtsev.core.timers.Tickable
import com.surovtsev.core.timers.TimeSpanHelper
import com.surovtsev.game.dagger.GameScope
import javax.inject.Inject

@GameScope
class TimeSpanHelperImp @Inject constructor(): TimeSpanHelper {

    override var timeAfterDeviceStartup: Long = 0L
        private set


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
