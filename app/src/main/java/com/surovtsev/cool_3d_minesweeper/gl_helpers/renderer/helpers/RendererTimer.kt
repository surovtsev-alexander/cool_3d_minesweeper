package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import android.os.SystemClock

class RendererTimer {

    var time = 0L

    init {
        updateTimer()
    }

    fun updateTimer() {
        time = SystemClock.elapsedRealtime()
    }
}
