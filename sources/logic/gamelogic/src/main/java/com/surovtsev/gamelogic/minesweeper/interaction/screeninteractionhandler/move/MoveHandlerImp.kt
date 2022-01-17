package com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.move

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
import com.surovtsev.touchlistener.helpers.handlers.MoveHandler
import com.surovtsev.utils.math.MatrixHelper
import glm_.vec2.Vec2
import javax.inject.Inject

@GameScope
class MoveHandlerImp @Inject constructor(
    private val cameraInfoHelperHolder: CameraInfoHelperHolder,
):
    MoveHandler
{
    override fun rotateBetweenProjections(prev: Vec2, curr: Vec2) {
        val cameraInfoHelper = cameraInfoHelperHolder.cameraInfoHelperFlow.value ?: return

        val nPrev = cameraInfoHelper.normalizedDisplayCoordinates(prev)
        val nCurr = cameraInfoHelper.normalizedDisplayCoordinates(curr)

        val prevFar = cameraInfoHelper.calcFarByProj(nPrev)
        val currFar = cameraInfoHelper.calcFarByProj(nCurr)


        val rotation = MatrixHelper.calcRotMatrix(prevFar, currFar)

        cameraInfoHelper.multiplyRotationMatrix(rotation)
    }

    override fun scale(factor: Float) {
        cameraInfoHelperHolder.cameraInfoHelperFlow.value?.scale(factor)
    }

    override fun move(proj1: Vec2, proj2: Vec2) {
        val cameraInfoHelper = cameraInfoHelperHolder.cameraInfoHelperFlow.value ?: return

        val nP1 = cameraInfoHelper.normalizedDisplayCoordinates(proj1)
        val nP2 = cameraInfoHelper.normalizedDisplayCoordinates(proj2)

        val p1Near = cameraInfoHelper.calcNearWorldPoint(nP1)
        val p2Near = cameraInfoHelper.calcNearWorldPoint(nP2)

        val diff = p2Near - p1Near


        cameraInfoHelper.translate(diff)
    }
}
