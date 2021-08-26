package com.surovtsev.cool_3d_minesweeper.math

import glm_.vec3.Vec3
import kotlin.math.PI
import kotlin.math.abs

object MyMath {
    val PIF = PI.toFloat()
    fun gradToRad(grad: Float) = grad * PIF / 180f

    val XRay = Vec3(1f, 0f, 0f)
    val YRay = Vec3(0f, 1f, 0f)
    val ZRay = Vec3(0f, 0f, 1f)

    val THRESHOLD = 1e-4

    fun isZero(x: Float) =
        abs(x) < THRESHOLD
}