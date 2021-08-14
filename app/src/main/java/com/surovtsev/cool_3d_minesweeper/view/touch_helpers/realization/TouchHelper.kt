package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.helper.DelayedRelease

abstract class TouchHelper:  DelayedRelease() {
    abstract fun onTouch(event: MotionEvent)
}
