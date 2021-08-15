package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.utils.DelayedRelease
import glm_.vec2.Vec2

abstract class TouchHelper:  DelayedRelease() {
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
