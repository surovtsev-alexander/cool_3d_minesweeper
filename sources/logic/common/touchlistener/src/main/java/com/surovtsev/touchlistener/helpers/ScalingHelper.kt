package com.surovtsev.touchlistener.helpers

import android.view.MotionEvent
import com.surovtsev.touchlistener.dagger.TouchListenerScope
import com.surovtsev.touchlistener.helpers.holders.HandlersHolder
import com.surovtsev.utils.math.MyMath
import glm_.vec2.Vec2
import javax.inject.Inject
import javax.inject.Named

@TouchListenerScope
class ScalingHelper @Inject constructor(
    private val handlersHolder: HandlersHolder,
    @Named(PrevDistance)
    private var prevDistance: Float,
    @Named(PrevCenter)
    private var prevCenter: Vec2
): TouchHelper() {
    companion object {
        const val PrevDistance = "prevDistance"
    }

    override fun onTouch(event: MotionEvent): TouchResult {
        val needToBeInitialized = getAndRelease()

        val a = getVec(event, 0)
        val b = getVec(event, 1)

        val distance = (a - b).length()
        val center = (a + b) / 2

        if (!needToBeInitialized) {
            if (!MyMath.isZero(prevDistance)) {
                val factor = distance / prevDistance
                handlersHolder.moveHandler?.scale(factor)
            }
            handlersHolder.moveHandler?.move(prevCenter, center)
        }

        prevDistance = distance
        prevCenter = center

        return TouchResult.None
    }
}