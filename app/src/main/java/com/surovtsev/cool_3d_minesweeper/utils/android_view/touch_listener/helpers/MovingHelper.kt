package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.move.MoveHandler
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameScope
import glm_.vec2.Vec2
import javax.inject.Inject
import javax.inject.Named

@GameScope
class MovingHelper @Inject constructor(
    private val moveHandler: MoveHandler,
    @Named(PrevCenter)
    private var prevCenter: Vec2
): TouchHelper() {

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