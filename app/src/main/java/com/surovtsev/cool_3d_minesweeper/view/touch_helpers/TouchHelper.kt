package com.surovtsev.cool_3d_minesweeper.view.touch_helpers

import android.view.MotionEvent

abstract class TouchHelper {
    abstract fun onTouch(event: MotionEvent)

    abstract fun release()
}