package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import android.opengl.Matrix
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig

class ClickHandler(val cameraInfo: CameraInfo) {
    val mMessagesComponent = ApplicationController.instance!!.messagesComponent!!

    fun handleClick(screenX: Float, screenY: Float) {
        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            mMessagesComponent.addMessageUI("args: $screenX $screenY")
        }

        fun normalizedDisplayCoordinates() = run {
            val pp = { p: Float, s: Float -> 2f * p / s - 1.0f}
            val x = pp(screenX, cameraInfo.mDisplayWidthF)
            val y = pp(screenY, cameraInfo.mDisplayHeightF) // * -1f

            floatArrayOf(x , y)
        }

        val normalizedCoordinates = normalizedDisplayCoordinates()
        val clipCoordinates = floatArrayOf(normalizedCoordinates[0], normalizedCoordinates[1]
            , 0f, 0f)

        fun toEyeCoordinates(): FloatArray {
            val invertedProjection = MatrixHelper.matrix_creator()
            Matrix.invertM(invertedProjection, 0,
                cameraInfo.mProjectionMatrix, 0)
            val eyeCoords = FloatArray(4)
            Matrix.multiplyMV(eyeCoords, 0,
                invertedProjection, 0,
                clipCoordinates, 0)
            return eyeCoords
        }

        val eyeCoords = toEyeCoordinates()

        fun toWorldRay(): FloatArray {
            val invertedView = MatrixHelper.matrix_creator()
            Matrix.invertM(invertedView, 0,
                cameraInfo.mViewMatrix, 0)
            val rayWorld = FloatArray(4)
            Matrix.multiplyMV(rayWorld, 0,
                invertedView, 0,
                eyeCoords, 0)
            return rayWorld
        }

        val worldRay = toWorldRay()
    }
}