package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.math.Math
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IMovingReceiver
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IRotationReceiver
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IScaleReceiver
import glm_.glm;
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3

class CameraInfo(displayWidth: Int, displayHeight: Int,
                 zNear: Float = 2f, zFar: Float = 20f) {

    val mMoveHandler = MoveHandler()

    var mScaleMatrix = MatrixHelper.identity_matrix()

    var mProjectionMatrix = MatrixHelper.zero_matrix()
    var mViewMatrix = MatrixHelper.zero_matrix()

    val mDisplayWidthF = displayWidth.toFloat()
    val mDisplayHeightF = displayHeight.toFloat()

    var mMVPMatrix = MatrixHelper.identity_matrix()
    var mIMVPMatrix = MatrixHelper.identity_matrix()

    init {
        glm.perspective(
            mProjectionMatrix,
            45f,
            mDisplayWidthF / mDisplayHeightF,
            zNear, zFar
        )

        mViewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)

        recalculateMVPMatrix()
    }

    inner class MoveHandler() :
        IRotationReceiver, IScaleReceiver, IMovingReceiver {
        private val COEFF = 15f
        var mUpdated = true
            private set

        fun identity_matrix() = MatrixHelper.identity_matrix()

        val I_MATRIX = identity_matrix()
        var rotMatrix = identity_matrix()
            private set

        var iRotMatrix = identity_matrix()
            private set

        private var x_axis = Math.XRay
        private var y_axis = Math.YRay

        override fun rotateBetweenProjections(prev: Vec2, curr: Vec2) {
            val nPrev = normalizedDisplayCoordinates(prev)
            val nCurr = normalizedDisplayCoordinates(curr)

            val prevFar = calc_far_by_proj(nPrev)
            val currFar = calc_far_by_proj(nCurr)

            if (LoggerConfig.LOG_MATRIX_HELPER) {
                Log.d(
                    "TEST",
                    "test\nprev: $prev\ncurr: $curr\nn_prev: $nPrev\nn_curr: $nCurr\nprev_far: $prevFar\ncurr_far: $currFar"
                )
            }

            val rotation = MatrixHelper.calc_rot_matrix(prevFar, currFar)
            rotMatrix = rotMatrix * rotation

            rotMatrix.inverse(iRotMatrix)

            mUpdated = true
        }

        override fun scale(factor: Float) {
            mScaleMatrix = mScaleMatrix.scale(factor)

            mUpdated = true
        }

        override fun move(proj1: Vec2, proj2: Vec2) {
            mUpdated = true
        }

        fun updateMatrix() {
            mUpdated = false
        }
    }

    fun recalculateMVPMatrix() {
        mMVPMatrix = mProjectionMatrix * mViewMatrix * mMoveHandler.rotMatrix * mScaleMatrix
        mIMVPMatrix = mMVPMatrix.inverse()
    }

    fun normalizedDisplayCoordinates(point: Vec2) = run {
        val pp = { p: Float, s: Float -> 2f * p / s - 1.0f }
        val x = pp(point.x, mDisplayWidthF)
        val y = pp(point.y, mDisplayHeightF) * -1f
        Vec2(x, y)
    }

    fun calc_near_by_proj(proj: Vec2) = calc_point_by_proj(-1f)(proj)
    fun calc_far_by_proj(proj: Vec2) = calc_point_by_proj(1f)(proj)

    fun calc_point_by_proj(z: Float): (Vec2) -> Vec3 {
        return fun(proj: Vec2): Vec3 = MatrixHelper.mult_mat4_vec3(
            mIMVPMatrix,
            Vec3(proj, z)
        )
    }
}