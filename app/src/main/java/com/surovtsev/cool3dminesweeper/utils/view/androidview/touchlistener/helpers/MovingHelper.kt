package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers

import android.view.MotionEvent
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.interfaces.MoveReceiver
import glm_.vec2.Vec2
import javax.inject.Inject
import javax.inject.Named

@GameScope
class MovingHelper @Inject constructor(
    private val moveReceiver: MoveReceiver,
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

        val needToBeInitialized = getAndRelease()
        if (!needToBeInitialized) {
            moveReceiver.move(
                prevCenter, currCenter
            )
        }

        prevCenter = currCenter
    }
}