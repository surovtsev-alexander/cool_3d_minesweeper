package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.math.Math
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IMoveReceiver
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IReceiverCalculator
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IScaleReceiver
import glm_.vec2.Vec2

class ScalingHelper(
    val scaleReceiverCalculator: IReceiverCalculator<IScaleReceiver>,
    val moveReceiverCalculator: IReceiverCalculator<IMoveReceiver>
): TouchHelper() {
    var prevDistance = 0f
    var prevCenter = Vec2()

    override fun onTouch(event: MotionEvent) {
        val needToBeInitted = getAndRelease()


        val a = getVec(event, 0)
        val b = getVec(event, 1)

        val distance = (a - b).length()
        val center = (a + b) / 2

        if (!needToBeInitted) {
            if (!Math.isZero(prevDistance)) {
                val factor = distance / prevDistance
                scaleReceiverCalculator.getReceiver()?.scale(factor)
            }
            moveReceiverCalculator.getReceiver()?.move(prevCenter, center)
        }

        prevDistance = distance
        prevCenter = center
    }
}