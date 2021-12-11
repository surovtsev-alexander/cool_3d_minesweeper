package com.surovtsev.touchlistener.helpers

import android.view.MotionEvent
import com.surovtsev.core.utils.statehelpers.UpdatableImp
import glm_.vec2.Vec2

abstract class TouchHelper:  UpdatableImp() {
    abstract fun onTouch(event: MotionEvent)

    companion object {
        const val PrevCenter = "prevCenter"

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
