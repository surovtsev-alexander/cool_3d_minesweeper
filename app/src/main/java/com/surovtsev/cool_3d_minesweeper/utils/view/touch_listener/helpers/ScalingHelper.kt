package com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IMoveReceiver
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IReceiverCalculator
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IScaleReceiver
import glm_.vec2.Vec2

class ScalingHelper(
    val scaleReceiverCalculator: IReceiverCalculator<IScaleReceiver>,
    val moveReceiverCalculator: IReceiverCalculator<IMoveReceiver>
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
                scaleReceiverCalculator.getReceiver()?.scale(factor)
            }
            moveReceiverCalculator.getReceiver()?.move(prevCenter, center)
        }

        prevDistance = distance
        prevCenter = center
    }
}