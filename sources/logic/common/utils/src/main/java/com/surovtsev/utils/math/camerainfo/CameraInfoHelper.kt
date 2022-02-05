package com.surovtsev.utils.math.camerainfo

import com.surovtsev.utils.gles.renderer.ScreenResolution
import com.surovtsev.utils.math.MatrixHelper
import com.surovtsev.utils.statehelpers.UpdatableImp
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import java.lang.Float.max

class CameraInfoHelper(
    val cameraInfo: CameraInfo,
    screenResolution: ScreenResolution,
):
    UpdatableImp()
{
    companion object {
        fun maxWithOneToAvoidDivisionByZero(
            x: Float
        ): Float {
            return max(1f, x)
        }
    }

    private val zNear: Float = 2f
    private val zFar: Float = 20f

    private var displayWidthF = maxWithOneToAvoidDivisionByZero(screenResolution[0].toFloat())
    private var displayHeightF = maxWithOneToAvoidDivisionByZero(screenResolution[1].toFloat())

    init {
        glm.perspective(
            cameraInfo.projectionMatrix,
            45f,
            displayWidthF / displayHeightF,
            zNear, zFar
        )
        cameraInfo.invProjectionMatrix = cameraInfo.projectionMatrix.inverse()

        cameraInfo.viewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)

        cameraInfo.recalculateMVPMatrix()

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

    private fun calcPointByProj(z: Float): (Vec2) -> Vec3 {
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