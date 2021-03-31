package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.Math
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import glm_.glm
import glm_.mat4x4.Mat4

class MoveHandler {
    private val COEFF = 15f
    var mXRotation = 0f
    var mYRotation = 0f
    var mUpdated = true
        private set

    fun matrix_creator() = MatrixHelper.matrix_creator()

    var mMatrix = matrix_creator()

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        mXRotation += deltaX / COEFF
        mYRotation += deltaY / COEFF

        mUpdated = true

        if (LoggerConfig.LOG_TOUCH_HANDLER_DATA) {
            ApplicationController.instance?.messagesComponent?.addMessageUI("$mXRotation\t$mYRotation")
        }
    }

    fun updateMatrix() {
        mMatrix = glm.rotate(Mat4(1f), Math.gradToRad(mYRotation), Math.XRay)
        mMatrix = glm.rotate(mMatrix, Math.gradToRad(mXRotation), Math.YRay)

        mUpdated = false
    }
}