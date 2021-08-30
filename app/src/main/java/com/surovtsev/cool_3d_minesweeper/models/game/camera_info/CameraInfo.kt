package com.surovtsev.cool_3d_minesweeper.models.game.camera_info

import com.surovtsev.cool_3d_minesweeper.utils.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import glm_.glm;
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import glm_.vec3.Vec3

class CameraInfo(
    val displaySize: Vec2i,
    zNear: Float = 2f,
    zFar: Float = 20f
):
    Updatable()
{
    val displayWidthF = displaySize.x.toFloat()
    val displayHeightF = displaySize.y.toFloat()

    var scaleMatrix = MatrixHelper.identityMatrix()
    var viewMatrix = MatrixHelper.identityMatrix()
    var moveMatrix = MatrixHelper.identityMatrix()
    var projectionMatrix = MatrixHelper.identityMatrix()
    private var invProjectionMatrix = MatrixHelper.identityMatrix()


    private fun identityMatix() = MatrixHelper.identityMatrix()

    var rotMatrix = identityMatix()

    var iRotMatrix = identityMatix()
        private set


    var MVP = MatrixHelper.identityMatrix()
    var IMVP = MatrixHelper.identityMatrix()

    init {
        glm.perspective(
            projectionMatrix,
            45f,
            displayWidthF / displayHeightF,
            zNear, zFar
        )
        invProjectionMatrix = projectionMatrix.inverse()

        viewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)

        recalculateMVPMatrix()
    }

    fun recalculateMVPMatrix() {
        MVP = projectionMatrix * moveMatrix * viewMatrix * rotMatrix * scaleMatrix
        IMVP = MVP.inverse()
    }

    fun normalizedDisplayCoordinates(point: Vec2) = run {
        val pp = { p: Float, s: Float -> 2f * p / s - 1.0f }
        val x = pp(point.x, displayWidthF)
        val y = pp(point.y, displayHeightF) * -1f
        Vec2(x, y)
    }

    fun calcNearByProj(proj: Vec2) = calcPointByProj(-1f)(proj)
    fun calcFarByProj(proj: Vec2) = calcPointByProj(1f)(proj)

    fun calcPointByProj(z: Float): (Vec2) -> Vec3 {
        return fun(proj: Vec2): Vec3 = MatrixHelper.multMat4Vec3(
            IMVP,
            Vec3(proj, z)
        )
    }

    fun calcNearWorldPoint(proj: Vec2) = MatrixHelper.multMat4Vec3(
        invProjectionMatrix,
        Vec3(proj, -1)
    )

    fun multiplyRotationMatrix(m: Mat4) {
        rotMatrix *= m
        rotMatrix.inverse(iRotMatrix)

        update()
    }

    fun scale(factor: Float) {
        scaleMatrix = scaleMatrix.scale(factor)

        update()
    }

    fun translate(diff: Vec3) {
        moveMatrix = moveMatrix.translate(diff)

        update()
    }
}