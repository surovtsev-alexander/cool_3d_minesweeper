package com.surovtsev.cool_3d_minesweeper.utils.math

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.tan

object MatrixHelper {
    fun perspectiveM(m: FloatArray, yFovInDegrees: Float, aspect: Float,
                     n: Float, f: Float) {
        val angleInRadians = MyMath.gradToRad(yFovInDegrees)

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

    fun zeroMatrix() = Mat4(0f)
    fun identityMatrix() = Mat4(1f)

    fun multMat4Vec3(mat: Mat4, vec: Vec3): Vec3 {
        val x = mat * Vec4(vec, 1.0);
        val res = Vec3(x) / x[3];
        return res
    }

    val I_M = identityMatrix()

    fun calcRotMatrix(a: Vec3, b: Vec3): Mat4 {
        val an = b.normalize()
        val bn = a.normalize()

        val dp = an.dot(bn)
        val angle = acos(dp)

        if (abs(angle) < 0.001f || angle.isNaN()) {
            val res = identityMatrix()

            return res
        }

        val axis = an.cross(bn)

        val res = I_M.rotate(angle, axis)

        return res
    }
}