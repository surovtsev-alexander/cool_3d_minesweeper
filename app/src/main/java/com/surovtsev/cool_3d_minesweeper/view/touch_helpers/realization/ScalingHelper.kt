package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.util.Log
import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.math.Math
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IReceiverCalculator
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IScaleReceiver
import glm_.vec2.Vec2

class ScalingHelper(
    val scaleReceiverCalculator: IReceiverCalculator<IScaleReceiver>
): TouchHelper() {
    var prevDistance = 0f

    override fun onTouch(event: MotionEvent) {
        val needToBeInitted = getAndFlush()

        val f: (Int) -> Vec2 = {
            Vec2(
                event.getX(it),
                event.getY(it)
            )
        }
        val distanceCalculator: () -> Float = {
            val a = f(0)
            val b = f(1)
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