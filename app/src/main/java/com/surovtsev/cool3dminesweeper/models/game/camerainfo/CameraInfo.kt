package com.surovtsev.cool3dminesweeper.models.game.camerainfo

import com.surovtsev.utils.math.MatrixHelper
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

class CameraInfo {
    companion object {
        private val identityMatrix = MatrixHelper::identityMatrix
    }

    var scaleMatrix = identityMatrix()
    var rotMatrix = identityMatrix()
    var viewMatrix = identityMatrix()
    var moveMatrix = identityMatrix()
    var projectionMatrix = identityMatrix()


    var mVP = identityMatrix()

    var invProjectionMatrix = identityMatrix()
    var invRotMatrix = identityMatrix()
    var invMVP = identityMatrix()

    fun recalculateMVPMatrix() {
        mVP = projectionMatrix * moveMatrix * viewMatrix * rotMatrix * scaleMatrix
        invMVP = mVP.inverse()
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
