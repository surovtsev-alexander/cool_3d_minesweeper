package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import android.opengl.Matrix
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper

class CameraInfo(val displayWidth: Int, val displayHeight: Int,
                 val zNear: Float = 2f, val zFar: Float = 20f) {
    val mProjectionMatrix = MatrixHelper.matrix_creator()
    val mViewMatrix = MatrixHelper.matrix_creator()
    val mViewProjectionMatrix = MatrixHelper.matrix_creator()

    val mDisplayWidthF = displayWidth.toFloat()
    val mDisplayHeightF = displayHeight.toFloat()

    init {
        if (false) {
            MatrixHelper.perspectiveM(
                mProjectionMatrix,
                45f,
                displayWidth.toFloat() / displayHeight.toFloat(),
                1f, 10f
            )
        } else {
            Matrix.perspectiveM(
                mProjectionMatrix,
                0, 30f,
                mDisplayWidthF / mDisplayHeightF, zNear, zFar
            )
        }
        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.translateM(mViewMatrix, 0, 0f, 0f, -10f)
        Matrix.multiplyMM(mViewProjectionMatrix, 0,
            mProjectionMatrix, 0,
            mViewMatrix, 0)
    }
}