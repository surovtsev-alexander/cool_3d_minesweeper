package com.surovtsev.cool_3d_minesweeper.utils.math

import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import kotlin.math.PI
import kotlin.math.abs

object MyMath {
    val PIF = PI.toFloat()
    fun gradToRad(grad: Float) = grad * PIF / 180f

    val XRay = Vec3(1f, 0f, 0f)
    val YRay = Vec3(0f, 1f, 0f)
    val ZRay = Vec3(0f, 0f, 1f)

    val Rays = arrayOf<Vec3>(
        XRay,
        YRay,
        ZRay
    )

    val THRESHOLD = 1e-4

    fun isZero(x: Float) =
        abs(x) < THRESHOLD

    fun isPointInCounts(x: Vec3i, counts: Vec3i): Boolean {
        for (i in 0 until 3) {
            val xi = x[i]
            val d = counts[i]

            if (xi < 0 || xi >= d) {
                return false
            }
        }
        return true
    }
}
