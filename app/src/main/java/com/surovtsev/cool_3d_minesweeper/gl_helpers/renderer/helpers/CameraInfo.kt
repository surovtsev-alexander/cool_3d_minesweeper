package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import glm_.glm;
import glm_.mat4x4.Mat4

class CameraInfo(val displayWidth: Int, val displayHeight: Int,
                 val zNear: Float = 2f, val zFar: Float = 20f, val moveHandler: MoveHandler) {

    var mProjectionMatrix = MatrixHelper.zero_matrix()
        private set
    var mViewMatrix = MatrixHelper.zero_matrix()
    val mViewProjectionMatrix = MatrixHelper.zero_matrix()
    val mInvertedProjectionMatrix = MatrixHelper.zero_matrix()
    val mInvertedViewMatrix = MatrixHelper.zero_matrix()

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

    fun recalculateViewMatrix() {
        mViewMatrix = glm.translate(Mat4(), 0f, 0f, -10f)
        mViewMatrix = mViewMatrix * moveHandler.rotMatrix
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


}