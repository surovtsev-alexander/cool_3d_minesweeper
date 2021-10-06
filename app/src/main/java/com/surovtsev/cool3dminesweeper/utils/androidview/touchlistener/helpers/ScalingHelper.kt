package com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers

import android.view.MotionEvent
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.move.MoveHandler
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.math.MyMath
import glm_.vec2.Vec2
import javax.inject.Inject
import javax.inject.Named

@GameScope
class ScalingHelper @Inject constructor(
    private val moveHandler: MoveHandler,
    @Named(PrevDistance)
    private var prevDistance: Float,
    @Named(PrevCenter)
    private var prevCenter: Vec2
): TouchHelper() {
    companion object {
        const val PrevDistance = "prevDistance"
    }

    override fun onTouch(event: MotionEvent) {
        val needToBeInitialized = getAndRelease()

        val a = getVec(event, 0)
        val b = getVec(event, 1)

        val distance = (a - b).length()
        val center = (a + b) / 2

        if (!needToBeInitialized) {
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