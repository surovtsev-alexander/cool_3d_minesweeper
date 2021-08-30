package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler

import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IMoveReceiver
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IRotationReceiver
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IScaleReceiver
import com.surovtsev.cool_3d_minesweeper.utils.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfoHelper
import glm_.vec2.Vec2

class MoveHandler(
    private val cameraInfoHelper: CameraInfoHelper
):
    IRotationReceiver,
    IScaleReceiver,
    IMoveReceiver
{
    override fun rotateBetweenProjections(prev: Vec2, curr: Vec2) {
        val nPrev = cameraInfoHelper.normalizedDisplayCoordinates(prev)
        val nCurr = cameraInfoHelper.normalizedDisplayCoordinates(curr)

        val prevFar = cameraInfoHelper.calcFarByProj(nPrev)
        val currFar = cameraInfoHelper.calcFarByProj(nCurr)


        val rotation = MatrixHelper.calcRotMatrix(prevFar, currFar)

        cameraInfoHelper.multiplyRotationMatrix(rotation)
    }

    override fun scale(factor: Float) {
        cameraInfoHelper.scale(factor)
    }

    override fun move(proj1: Vec2, proj2: Vec2) {
        val nP1 = cameraInfoHelper.normalizedDisplayCoordinates(proj1)
        val nP2 = cameraInfoHelper.normalizedDisplayCoordinates(proj2)

        val p1Near = cameraInfoHelper.calcNearWorldPoint(nP1)
        val p2Near = cameraInfoHelper.calcNearWorldPoint(nP2)

        val diff = p2Near - p1Near


        cameraInfoHelper.translate(diff)
    }
}
