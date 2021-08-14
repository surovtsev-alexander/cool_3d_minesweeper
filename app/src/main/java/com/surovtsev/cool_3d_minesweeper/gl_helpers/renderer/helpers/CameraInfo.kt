package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.Math
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.IRotationReceiver
import glm_.glm;
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3

class CameraInfo(val displayWidth: Int, val displayHeight: Int,
                 val zNear: Float = 2f, val zFar: Float = 20f) {

    val mMoveHandler = MoveHandler()

    var mProjectionMatrix = MatrixHelper.zero_matrix()
        private set
    var mViewMatrix = MatrixHelper.zero_matrix()
    val mViewProjectionMatrix = MatrixHelper.zero_matrix()
    val mInvertedProjectionMatrix = MatrixHelper.zero_matrix()
    val mInvertedViewMatrix = MatrixHelper.zero_matrix()

    val mInvertedRMatrix = MatrixHelper.zero_matrix()

    val mDisplayWidthF = displayWidth.toFloat()
    val mDisplayHeightF = displayHeight.toFloat()

    init {
        glm.perspective(
            mProjectionMatrix,
            45f,
            mDisplayWidthF / mDisplayHeightF,
            zNear, zFar
        )
        mProjectionMatrix.inverse(mInvertedProjectionMatrix)
        recalculateViewMatrix()
    }

    inner class MoveHandler(): IRotationReceiver {
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

            val prevNear = calc_near_model_point_by_proj(nPrev)
            val currNear = calc_near_model_point_by_proj(nCurr)

            if (LoggerConfig.LOG_MATRIX_HELPER) {
                Log.d("TEST","test\nprev: $prev\ncurr: $curr\nn_prev: $nPrev\nn_curr: $nCurr\nprev_near: $prevNear\ncurr_near: $currNear")
            }

            val rotation = MatrixHelper.calc_rot_matrix(prevNear, currNear)
            rotMatrix = rotMatrix * rotation

            rotMatrix.inverse(iRotMatrix)

            mUpdated = true
        }

        fun handleTouchDrag_old(prev: Vec2, curr: Vec2) {
            val delta = (curr - prev) / COEFF

            rotMatrix = rotMatrix
                .rotate(Math.gradToRad(delta[0]), y_axis)
                .rotate(Math.gradToRad(delta[1]), x_axis)

            /*
            val iRotMatrix = Mat4()
            rotMatrix.inverse(iRotMatrix)
            x_axis = MatrixHelper.mult_mat4_vec3(iRotMatrix, x_axis)
            y_axis = MatrixHelper.mult_mat4_vec3(iRotMatrix, y_axis)
             */

            mUpdated = true

            if (LoggerConfig.LOG_TOUCH_HANDLER_DATA) {
                ApplicationController.try_to_add_message_to_component("$rotMatrix")
            }
        }

        fun updateMatrix() {
            mUpdated = false
        }
    }

    fun recalculateViewMatrix() {
        mViewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)
        mViewMatrix = mViewMatrix * mMoveHandler.rotMatrix
        Mat4.times(
            mViewProjectionMatrix,
            Mat4(mProjectionMatrix),
            Mat4(mViewMatrix)
        )

        mViewMatrix.inverse(mInvertedViewMatrix)

        if (LoggerConfig.LOG_CAMERA_INFO_DATA) {
            val messages = arrayOf<String>(
                (mProjectionMatrix * mInvertedProjectionMatrix).toString(),
                (mInvertedProjectionMatrix * mProjectionMatrix).toString(),
                (mViewMatrix * mInvertedViewMatrix).toString(),
                (mInvertedViewMatrix * mViewMatrix).toString())

            ApplicationController.instance!!.messagesComponent!!.addMessageUI(
                messages.reduce { acc, x -> "$acc\n$x"}
            )
        }
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
        return fun (proj: Vec2): Vec3 {
            val vpx = Vec3(proj, z)
            val vx = MatrixHelper.mult_mat4_vec3(mInvertedProjectionMatrix, vpx)
            return MatrixHelper.mult_mat4_vec3(mInvertedViewMatrix, vx)
        }
    }

    fun calc_near_model_point_by_proj(proj: Vec2): Vec3 {
        val vpx = Vec3(proj, -1)
        val vx = MatrixHelper.mult_mat4_vec3(mInvertedProjectionMatrix, vpx)
        return MatrixHelper.mult_mat4_vec3(mMoveHandler.iRotMatrix, vx)
    }


}