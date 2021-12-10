package com.surovtsev.utils.math

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlin.math.acos
import kotlin.math.sqrt

@Suppress("unused", "MemberVisibilityCanBePrivate")
object RotationMatrixDecomposer {
    fun convSinCos(sc: Float) = sqrt(1.0f - sc * sc)

    fun angleBySinCos(sv: Float, cv: Float): Float {
        val res = acos(cv)
        if (sv < 0) {
            return -1f * res
        }
        return res
    }

    fun getAngles(mat: Mat4): Vec3 {
        //transposed matrix
        val sy = mat[2][0]
        val cy = convSinCos(sy)
        val y = angleBySinCos(sy, cy)


        val sx: Float
        val cx: Float
        val sz: Float
        val cz: Float
        val x: Float
        val z: Float

        if (MyMath.isZero(cy)) {
            if (sy > 0.0f) {
                sx = mat[0, 1]
                cx = mat[1, 1]
            } else {
                sx = mat[1, 2]
                cx = mat[0, 2]
            }
            x = angleBySinCos(sx, cx)
            z = 0f
        }
        else {
            sx = -1 * mat[2, 1] / cy
            cx = mat[2, 2] / cy
            sz = -1 * mat[1, 0] / cy
            cz = mat[0, 0] / cy

            x = angleBySinCos(sx, cx)
            z = angleBySinCos(sz, cz)
        }

        return Vec3(x, y, z)
    }
}