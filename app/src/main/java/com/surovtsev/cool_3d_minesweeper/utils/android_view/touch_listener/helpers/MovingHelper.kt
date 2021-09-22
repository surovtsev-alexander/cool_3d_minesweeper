package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerScope
import glm_.vec2.Vec2
import javax.inject.Inject

@GameControllerScope
class MovingHelper @Inject constructor(
    private val moveHandler: MoveHandler
): TouchHelper() {

    private var prevCenter = Vec2()

    override fun onTouch(event: MotionEvent) {
        val pointerCount = event.pointerCount
        val points = (0 until pointerCount).map {
            getVec(event, it)
        }
        val sum = points.fold(Vec2()) { sum, elem -> sum + elem }
        val currCenter = sum / pointerCount

        val needToBeInited = getAndRelease()
        if (!needToBeInited) {
            moveHandler.move(
                prevCenter, currCenter
            )
        }

        prevCenter = currCenter
    }
}