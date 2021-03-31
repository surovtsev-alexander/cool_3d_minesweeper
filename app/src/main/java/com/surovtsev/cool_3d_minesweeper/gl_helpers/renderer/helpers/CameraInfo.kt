package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import android.opengl.Matrix
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import glm_.glm;
import glm_.mat4x4.Mat4

class CameraInfo(val displayWidth: Int, val displayHeight: Int,
                 val zNear: Float = 2f, val zFar: Float = 20f) {

    var mProjectionMatrix = MatrixHelper.matrix_creator()
        private set
    var mViewMatrix = MatrixHelper.matrix_creator()
    val mViewProjectionMatrix = Mat4()

    val mDisplayWidthF = displayWidth.toFloat()
    val mDisplayHeightF = displayHeight.toFloat()

    init {
        glm.perspective(
            mProjectionMatrix,
            45f,
            mDisplayWidthF / mDisplayHeightF,
            zNear, zFar
        )
        mViewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)
        Mat4.times(
            mViewProjectionMatrix,
            Mat4(mProjectionMatrix),
            Mat4(mViewMatrix)
        )
    }
}