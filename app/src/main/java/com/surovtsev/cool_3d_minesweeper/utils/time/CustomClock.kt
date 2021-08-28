package com.surovtsev.cool_3d_minesweeper.utils.time

import android.os.SystemClock

class CustomClock {

    var time = 0L

    init {
        updateTime()
    }

    fun updateTime() {
        time = SystemClock.elapsedRealtime()
    }
}
