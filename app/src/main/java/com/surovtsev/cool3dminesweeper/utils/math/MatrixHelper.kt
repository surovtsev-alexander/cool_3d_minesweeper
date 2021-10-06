package com.surovtsev.cool3dminesweeper.utils.math

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.abs
import kotlin.math.acos


object MatrixHelper {
    fun identityMatrix() = Mat4(1f)

    @Suppress("SpellCheckingInspection")
    fun multMat4Vec3(mat: Mat4, vec: Vec3): Vec3 {
        val x = mat * Vec4(vec, 1.0)
        return Vec3(x) / x[3]
    }

    private val I_M = identityMatrix()

    fun calcRotMatrix(a: Vec3, b: Vec3): Mat4 {
        val an = b.normalize()
        val bn = a.normalize()

        val dp = an.dot(bn)
        val angle = acos(dp)

        if (abs(angle) < 0.001f || angle.isNaN()) {
            return identityMatrix()
        }

        val axis = an.cross(bn)

        return I_M.rotate(angle, axis)
    }
}