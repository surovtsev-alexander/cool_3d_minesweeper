package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import glm_.vec2.Vec2

abstract class TouchHelper:  Updatable() {
    abstract fun onTouch(event: MotionEvent)

    companion object {
        fun getVec(event: MotionEvent) = Vec2(
            event.x,
            event.y
        )

        fun getVec(event: MotionEvent, i: Int) = Vec2(
            event.getX(i),
            event.getY(i)
        )
    }
}
