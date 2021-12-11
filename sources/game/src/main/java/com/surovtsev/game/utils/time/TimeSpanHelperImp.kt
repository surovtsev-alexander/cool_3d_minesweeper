package com.surovtsev.game.utils.time

import android.os.SystemClock
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.utils.timers.Tickable
import com.surovtsev.utils.timers.TimeSpanHelper
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
