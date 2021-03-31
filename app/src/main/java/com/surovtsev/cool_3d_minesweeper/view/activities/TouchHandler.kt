package com.surovtsev.cool_3d_minesweeper.view.activities

import android.opengl.Matrix
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController

class TouchHandler {
    val COEFF = 15f
    var _xRotation = 0f
    var _yRotation = 0f
    var _updated = true
        private set

    fun matrix_creator() = FloatArray(16) { 0f }

    var _matrix = matrix_creator()

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        _xRotation += deltaX / COEFF
        _yRotation += deltaY / COEFF

        _updated = true

        if (true) {
            ApplicationController.instance?.messagesComponent?.addMessageUI("$_xRotation\t$_yRotation")
        }
    }

    fun updateMatrix() {
        Matrix.setIdentityM(_matrix, 0)
        Matrix.rotateM(_matrix, 0, _yRotation, 1f, 0f, 0f)
        Matrix.rotateM(_matrix, 0, _xRotation, 0f, 1f, 0f)

        _updated = false
    }
}