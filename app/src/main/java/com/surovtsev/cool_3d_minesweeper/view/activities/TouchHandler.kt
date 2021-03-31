package com.surovtsev.cool_3d_minesweeper.view.activities

import android.opengl.Matrix
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig

class TouchHandler {
    val COEFF = 15f
    var mXRotation = 0f
    var mYRotation = 0f
    var mUpdated = true
        private set

    fun matrix_creator() = FloatArray(16) { 0f }

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
        Matrix.setIdentityM(mMatrix, 0)
        Matrix.rotateM(mMatrix, 0, mYRotation, 1f, 0f, 0f)
        Matrix.rotateM(mMatrix, 0, mXRotation, 0f, 1f, 0f)

        mUpdated = false
    }
}