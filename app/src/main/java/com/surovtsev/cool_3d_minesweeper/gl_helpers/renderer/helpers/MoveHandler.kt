package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.Math
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import glm_.vec2.Vec2

class MoveHandler() {
    private val COEFF = 15f
    var mUpdated = true
        private set

    fun identity_matrix() = MatrixHelper.identity_matrix()

    val I_MATRIX = identity_matrix()
    var rotMatrix = identity_matrix()
        private set

    private var x_axis = Math.XRay
    private var y_axis = Math.YRay
    private var z_axis = Math.ZRay

    fun handleTouchDrag(prev: Vec2, curr: Vec2) {
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