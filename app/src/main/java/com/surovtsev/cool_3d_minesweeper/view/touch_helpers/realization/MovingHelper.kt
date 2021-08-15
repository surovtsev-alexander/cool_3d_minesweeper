package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IMovingReceiver
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IReceiverCalculator
import glm_.vec2.Vec2

class MovingHelper(
    val movingReceiverCalculator: IReceiverCalculator<IMovingReceiver>
): TouchHelper() {

    var prevCenter = Vec2()

    override fun onTouch(event: MotionEvent) {

        val needToBeInited = getAndFlush()

        val pointerCount = event.pointerCount
        val points = (0 until pointerCount).map {
            getVec(event, it)
        }
        val sum = points.fold(Vec2()) { sum, elem -> sum + elem }
        val currCenter = sum / pointerCount

        if (!needToBeInited) {
            movingReceiverCalculator.getReceiver()?.move(
                prevCenter, currCenter
            )
        }

        prevCenter = currCenter
    }
}