package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import glm_.glm;
import glm_.mat4x4.Mat4

class CameraInfo(val displayWidth: Int, val displayHeight: Int,
                 val zNear: Float = 2f, val zFar: Float = 20f) {

    var mProjectionMatrix = MatrixHelper.matrix_creator()
        private set
    var mViewMatrix = MatrixHelper.matrix_creator()
    val mViewProjectionMatrix = MatrixHelper.matrix_creator()
    val mInvertedProjectionMatrix = MatrixHelper.matrix_creator()
    val mInvertedViewMatrix = MatrixHelper.matrix_creator()

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

        mProjectionMatrix.inverse(mInvertedProjectionMatrix)
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
}