package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers

import com.surovtsev.cool_3d_minesweeper.dagger.app.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.utils.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import glm_.glm;
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import glm_.vec3.Vec3
import javax.inject.Inject

@GameControllerScope
class CameraInfoHelper @Inject constructor(
    val cameraInfo: CameraInfo,
):
    Updatable()
{
    private val zNear: Float = 2f
    private val zFar: Float = 20f

    private var _displaySize: Vec2i = Vec2i(-1, -1)
    var displaySize: Vec2i
        get() = _displaySize
        private set(value) {
            _displaySize = value
            displayWidthF = displaySize[0].toFloat()
            displayHeightF = displaySize[1].toFloat()
        }
    private var displayWidthF = displaySize[0].toFloat()
    private var displayHeightF = displaySize[1].toFloat()

    fun onSurfaceChanged(newDisplaySize: Vec2i) {
        if (displaySize != newDisplaySize) {

            displaySize = newDisplaySize

            glm.perspective(
                cameraInfo.projectionMatrix,
                45f,
                displayWidthF / displayHeightF,
                zNear, zFar
            )
            cameraInfo.invProjectionMatrix = cameraInfo.projectionMatrix.inverse()

            cameraInfo.viewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)

            cameraInfo.recalculateMVPMatrix()
        }

        update()
    }

    fun normalizedDisplayCoordinates(point: Vec2) = run {
        val pp = { p: Float, s: Float -> 2f * p / s - 1.0f }
        val x = pp(point[0], displayWidthF)
        val y = pp(point[1], displayHeightF) * -1f
        Vec2(x, y)
    }

    fun calcNearByProj(proj: Vec2) = calcPointByProj(-1f)(proj)
    fun calcFarByProj(proj: Vec2) = calcPointByProj(1f)(proj)

    fun calcPointByProj(z: Float): (Vec2) -> Vec3 {
        return fun(proj: Vec2): Vec3 = MatrixHelper.multMat4Vec3(
            cameraInfo.invMVP,
            Vec3(proj, z)
        )
    }

    fun calcNearWorldPoint(proj: Vec2) = MatrixHelper.multMat4Vec3(
        cameraInfo.invProjectionMatrix,
        Vec3(proj, -1)
    )

    fun multiplyRotationMatrix(m: Mat4) {
        cameraInfo.multiplyRotationMatrix(m)

        update()
    }

    fun scale(factor: Float) {
        cameraInfo.scale(factor)

        update()
    }

    fun translate(diff: Vec3) {
        cameraInfo.translate(diff)

        update()
    }
}