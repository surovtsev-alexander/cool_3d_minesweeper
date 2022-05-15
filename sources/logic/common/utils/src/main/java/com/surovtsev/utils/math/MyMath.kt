package com.surovtsev.utils.math

import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import kotlin.math.PI
import kotlin.math.abs

object MyMath {
    private const val PIF = PI.toFloat()
    @Suppress("unused")
    fun gradToRad(grad: Float) = grad * PIF / 180f

    private val XRay = Vec3(1f, 0f, 0f)
    private val YRay = Vec3(0f, 1f, 0f)
    private val ZRay = Vec3(0f, 0f, 1f)

    val Rays = listOf(
        XRay,
        YRay,
        ZRay
    )

    private const val THRESHOLD = 1e-4

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
