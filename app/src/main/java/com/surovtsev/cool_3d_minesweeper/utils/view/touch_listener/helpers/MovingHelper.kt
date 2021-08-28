package com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IMoveReceiver
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IReceiverCalculator
import glm_.vec2.Vec2

class MovingHelper(
    private val movingReceiverCalculator: IReceiverCalculator<IMoveReceiver>
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
            movingReceiverCalculator.getReceiver()?.move(
                prevCenter, currCenter
            )
        }

        prevCenter = currCenter
    }
}