package com.surovtsev.cool_3d_minesweeper.models.game.camera_info

import com.surovtsev.cool_3d_minesweeper.utils.math.MatrixHelper
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

class CameraInfo() {
    companion object {
        private val identityMatrix = MatrixHelper::identityMatrix
    }

    var scaleMatrix = identityMatrix()
    var viewMatrix = identityMatrix()
    var moveMatrix = identityMatrix()
    var projectionMatrix = identityMatrix()
    var invProjectionMatrix = identityMatrix()
    var rotMatrix = identityMatrix()
    var iRotMatrix = identityMatrix()
    var MVP = identityMatrix()
    var IMVP = identityMatrix()

    fun recalculateMVPMatrix() {
        MVP = projectionMatrix * moveMatrix * viewMatrix * rotMatrix * scaleMatrix
        IMVP = MVP.inverse()
    }

    fun multiplyRotationMatrix(m: Mat4) {
        rotMatrix *= m
        rotMatrix.inverse(iRotMatrix)
    }

    fun scale(factor: Float) {
        scaleMatrix = scaleMatrix.scale(factor)
    }

    fun translate(diff: Vec3) {
        moveMatrix = moveMatrix.translate(diff)
    }
}
