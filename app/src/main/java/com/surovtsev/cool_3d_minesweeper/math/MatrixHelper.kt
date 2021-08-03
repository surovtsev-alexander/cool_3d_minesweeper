package com.surovtsev.cool_3d_minesweeper.math

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.tan

object MatrixHelper {
    fun perspectiveM(m: FloatArray, yFovInDegrees: Float, aspect: Float,
                     n: Float, f: Float) {
        val angleInRadians = Math.gradToRad(yFovInDegrees)

        val a = (1.0 / tan(angleInRadians / 2.0)).toFloat()

        m[0] = a / aspect
        m[1] = 0f
        m[2] = 0f
        m[3] = 0f

        m[4] = 0f
        m[5] = a
        m[6] = 0f
        m[7] = 0f

        m[8] = 0f
        m[9] = 0f
        m[10] = -((f + n) / (f - n))
        m[11] = -1f

        m[12] = 0f
        m[13] = 0f
        m[14] = -((2f * f * n) / (f - n))
        m[15] = 0f
    }

    fun zero_matrix() = Mat4(0f)
    fun identity_matrix() = Mat4(1f)

    fun mult_mat4_vec3(mat: Mat4, vec: Vec3): Vec3 {
        val x = mat * Vec4(vec, 1.0);
        val res = Vec3(x) / x[3];
        return res
    }
}