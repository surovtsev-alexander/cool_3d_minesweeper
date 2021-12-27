package com.surovtsev.gamelogic.models.game.save.helpers

import com.surovtsev.utils.math.camerainfo.CameraInfo
import glm_.mat4x4.Mat4
import glm_.vec4.Vec4

data class Mat4ToSave(
    val v1: Vec4,
    val v2: Vec4,
    val v3: Vec4,
    val v4: Vec4
) {
    @Suppress("ReplaceGetOrSet")
    constructor(m: Mat4): this(
        m.get(0), m.get(1), m.get(2), m.get(3)
    )

    fun getMatrix() = Mat4(v1, v2, v3, v4)
}

class CameraInfoToSave(
    private val scaleMatrix: Mat4ToSave,
    private val rotMatrix: Mat4ToSave,
    private val viewMatrix: Mat4ToSave,
    private val moveMatrix: Mat4ToSave,
    private val projectionMatrix: Mat4ToSave
) {
    companion object {
        fun createObject(cameraInfo: CameraInfo): CameraInfoToSave {
            return CameraInfoToSave(
                Mat4ToSave(cameraInfo.scaleMatrix),
                Mat4ToSave(cameraInfo.rotMatrix),
                Mat4ToSave(cameraInfo.viewMatrix),
                Mat4ToSave(cameraInfo.moveMatrix),
                Mat4ToSave(cameraInfo.projectionMatrix)
            )
        }
    }

    fun getCameraInfo(): CameraInfo {
        val res = CameraInfo()
        res.scaleMatrix = scaleMatrix.getMatrix()
        res.rotMatrix = rotMatrix.getMatrix()
        res.viewMatrix = viewMatrix.getMatrix()
        res.moveMatrix = moveMatrix.getMatrix()
        res.projectionMatrix = projectionMatrix.getMatrix()

        res.invProjectionMatrix = res.projectionMatrix.inverse()
        res.invRotMatrix = res.rotMatrix.inverse()
        res.recalculateMVPMatrix()
        res.invMVP = res.mVP.inverse()

        return res
    }
}