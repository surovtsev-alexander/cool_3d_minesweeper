package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.math.Math
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IReceiverCalculator
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IScaleReceiver

class ScalingHelper(
    val scaleReceiverCalculator: IReceiverCalculator<IScaleReceiver>
): TouchHelper() {
    var prevDistance = 0f

    override fun onTouch(event: MotionEvent) {
        val needToBeInitted = getAndRelease()

        val distanceCalculator: () -> Float = {
            val a = getVec(event, 0)
            val b = getVec(event, 1)
            (a - b).length()
        }

        val distance = distanceCalculator()

        if (needToBeInitted) {
            prevDistance = distance
            return
        }

        if (!Math.isZero(prevDistance)) {
            val factor = distance / prevDistance
            scaleReceiverCalculator.getReceiver()?.scale(factor)
        }

        prevDistance = distance
    }
}