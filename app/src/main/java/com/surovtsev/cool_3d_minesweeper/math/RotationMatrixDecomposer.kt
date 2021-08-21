package com.surovtsev.cool_3d_minesweeper.math

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import java.lang.Math
import kotlin.math.acos

object RotationMatrixDecomposer {
    fun conv_sin_cos(sc: Float) = Math.sqrt(1.0 - sc * sc).toFloat()

    fun angle_by_sin_cos(sv: Float, cv: Float): Float {
        val res = acos(cv)
        if (sv < 0) {
            return -1f * res
        }
        return res
    }

    fun getAngles(mat: Mat4): Vec3 {
        //transposed matrix
        val sy = mat[2][0]
        val cy = conv_sin_cos(sy)
        val y = angle_by_sin_cos(sy, cy)


        var sx = 0f
        var cx = 0f
        var sz = 0f
        var cz = 0f
        var x = 0f
        var z = 0f

        if (MyMath.isZero(cy)) {
            if (sy > 0.0f) {
                sx = mat[0, 1]
                cx = mat[1, 1]
            } else {
                sx = mat[1, 2]
                cx = mat[0, 2]
            }
            x = angle_by_sin_cos(sx, cx)
            z = 0f
        }
        else {
            sx = -1 * mat[2, 1] / cy
            cx = mat[2, 2] / cy
            sz = -1 * mat[1, 0] / cy
            cz = mat[0, 0] / cy

            x = angle_by_sin_cos(sx, cx)
            z = angle_by_sin_cos(sz, cz)
        }

        return Vec3(x, y, z)
    }
}