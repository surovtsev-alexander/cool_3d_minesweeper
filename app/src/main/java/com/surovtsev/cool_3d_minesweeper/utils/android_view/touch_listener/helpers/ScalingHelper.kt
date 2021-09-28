package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import glm_.vec2.Vec2
import javax.inject.Inject

@GameControllerScope
class ScalingHelper @Inject constructor(
    private val moveHandler: MoveHandler
): TouchHelper() {
    private var prevDistance = 0f
    private var prevCenter = Vec2()

    override fun onTouch(event: MotionEvent) {
        val needToBeInitted = getAndRelease()


        val a = getVec(event, 0)
        val b = getVec(event, 1)

        val distance = (a - b).length()
        val center = (a + b) / 2

        if (!needToBeInitted) {
            if (!MyMath.isZero(prevDistance)) {
                val factor = distance / prevDistance
                moveHandler.scale(factor)
            }
            moveHandler.move(prevCenter, center)
        }

        prevDistance = distance
        prevCenter = center
    }
}