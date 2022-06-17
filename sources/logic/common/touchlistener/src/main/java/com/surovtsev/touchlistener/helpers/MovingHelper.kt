package com.surovtsev.touchlistener.helpers

import android.view.MotionEvent
import com.surovtsev.touchlistener.dagger.TouchListenerScope
import com.surovtsev.touchlistener.helpers.holders.HandlersHolder
import glm_.vec2.Vec2
import javax.inject.Inject
import javax.inject.Named

@TouchListenerScope
class MovingHelper @Inject constructor(
    private val handlesHolder: HandlersHolder,
    @Named(PrevCenter)
    private var prevCenter: Vec2
): TouchHelper() {

    override fun onTouch(event: MotionEvent): TouchResult {
        val pointerCount = event.pointerCount
        val points = (0 until pointerCount).map {
            getVec(event, it)
        }
        val sum = points.fold(Vec2()) { sum, elem -> sum + elem }
        val currCenter = sum / pointerCount

        val needToBeInitialized = getAndRelease()
        if (!needToBeInitialized) {
            handlesHolder.moveHandler?.move(
                prevCenter, currCenter
            )
        }

        prevCenter = currCenter

        return TouchResult.None
    }
}