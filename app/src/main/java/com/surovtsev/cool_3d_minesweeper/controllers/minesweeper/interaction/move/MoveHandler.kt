package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler

import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IMoveReceiver
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IRotationReceiver
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IScaleReceiver
import com.surovtsev.cool_3d_minesweeper.utils.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import glm_.vec2.Vec2

class MoveHandler(
    private val cameraInfo: CameraInfo
):
    IRotationReceiver,
    IScaleReceiver,
    IMoveReceiver
{
    override fun rotateBetweenProjections(prev: Vec2, curr: Vec2) {
        val nPrev = cameraInfo.normalizedDisplayCoordinates(prev)
        val nCurr = cameraInfo.normalizedDisplayCoordinates(curr)

        val prevFar = cameraInfo.calcFarByProj(nPrev)
        val currFar = cameraInfo.calcFarByProj(nCurr)


        val rotation = MatrixHelper.calcRotMatrix(prevFar, currFar)

        cameraInfo.multiplyRotationMatrix(rotation)
    }

    override fun scale(factor: Float) {
        cameraInfo.scale(factor)
    }

    override fun move(proj1: Vec2, proj2: Vec2) {
        val nP1 = cameraInfo.normalizedDisplayCoordinates(proj1)
        val nP2 = cameraInfo.normalizedDisplayCoordinates(proj2)

        val p1Near = cameraInfo.calcNearWorldPoint(nP1)
        val p2Near = cameraInfo.calcNearWorldPoint(nP2)

        val diff = p2Near - p1Near


        cameraInfo.translate(diff)
    }
}
