package com.surovtsev.cool_3d_minesweeper.models.game.camera_info

import com.surovtsev.cool_3d_minesweeper.utils.math.MatrixHelper
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

class CameraInfo() {
    companion object {
        private val identityMatrix = MatrixHelper::identityMatrix
    }

    var scaleMatrix = identityMatrix()
    var rotMatrix = identityMatrix()
    var viewMatrix = identityMatrix()
    var moveMatrix = identityMatrix()
    var projectionMatrix = identityMatrix()


    var MVP = identityMatrix()

    var invProjectionMatrix = identityMatrix()
    var invRotMatrix = identityMatrix()
    var invMVP = identityMatrix()

    fun recalculateMVPMatrix() {
        MVP = projectionMatrix * moveMatrix * viewMatrix * rotMatrix * scaleMatrix
        invMVP = MVP.inverse()
    }

    fun multiplyRotationMatrix(m: Mat4) {
        rotMatrix *= m
        rotMatrix.inverse(invRotMatrix)
    }

    fun scale(factor: Float) {
        scaleMatrix = scaleMatrix.scale(factor)
    }

    fun translate(diff: Vec3) {
        moveMatrix = moveMatrix.translate(diff)
    }
}
