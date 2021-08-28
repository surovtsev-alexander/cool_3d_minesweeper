package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.utils.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.utils.DelayedRelease
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IMoveReceiver
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IRotationReceiver
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.IScaleReceiver
import glm_.glm;
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3

class CameraInfo(displayWidth: Int, displayHeight: Int,
                 zNear: Float = 2f, zFar: Float = 20f) {
    val displayWidthF = displayWidth.toFloat()
    val displayHeightF = displayHeight.toFloat()


    val moveHandler = MoveHandler()

    var scaleMatrix = MatrixHelper.identityMatrix()
    var viewMatrix = MatrixHelper.identityMatrix()
    var moveMatrix = MatrixHelper.identityMatrix()
    var projectionMatrix = MatrixHelper.identityMatrix()
    var IProjectionMatrix = MatrixHelper.identityMatrix()


    var MVP = MatrixHelper.identityMatrix()
    var IMVP = MatrixHelper.identityMatrix()

    init {
        glm.perspective(
            projectionMatrix,
            45f,
            displayWidthF / displayHeightF,
            zNear, zFar
        )
        IProjectionMatrix = projectionMatrix.inverse()

        viewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)

        recalculateMVPMatrix()
    }

    inner class MoveHandler(): DelayedRelease(),
        IRotationReceiver, IScaleReceiver, IMoveReceiver {
        private val COEFF = 15f

        fun identity_matrix() = MatrixHelper.identityMatrix()

        var rotMatrix = identity_matrix()
            private set

        var iRotMatrix = identity_matrix()
            private set

        override fun rotateBetweenProjections(prev: Vec2, curr: Vec2) {
            val nPrev = normalizedDisplayCoordinates(prev)
            val nCurr = normalizedDisplayCoordinates(curr)

            val prevFar = calcFarByProj(nPrev)
            val currFar = calcFarByProj(nCurr)

            if (LoggerConfig.LOG_MATRIX_HELPER) {
                Log.d(
                    "TEST",
                    "test\nprev: $prev\ncurr: $curr\nn_prev: $nPrev\nn_curr: $nCurr\nprev_far: $prevFar\ncurr_far: $currFar"
                )
            }

            val rotation = MatrixHelper.calcRotMatrix(prevFar, currFar)
            rotMatrix = rotMatrix * rotation

            rotMatrix.inverse(iRotMatrix)

            update()
        }

        override fun scale(factor: Float) {
            scaleMatrix = scaleMatrix.scale(factor)

            update()
        }

        override fun move(proj1: Vec2, proj2: Vec2) {
            val nP1 = normalizedDisplayCoordinates(proj1)
            val nP2 = normalizedDisplayCoordinates(proj2)

            val p1Near = calcNearWorldPoint(nP1)
            val p2Near = calcNearWorldPoint(nP2)

            val diff = p2Near - p1Near

            moveMatrix = moveMatrix.translate(diff)

            update()
        }
    }

    fun recalculateMVPMatrix() {
        MVP = projectionMatrix * moveMatrix * viewMatrix * moveHandler.rotMatrix * scaleMatrix
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
        IProjectionMatrix,
        Vec3(proj, -1)
    )
}