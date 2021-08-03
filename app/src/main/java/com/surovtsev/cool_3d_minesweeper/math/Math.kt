package com.surovtsev.cool_3d_minesweeper.math

import glm_.vec3.Vec3
import kotlin.math.PI

object Math {
    val PIF = PI.toFloat()
    fun gradToRad(grad: Float) = grad * PIF / 180f

    val XRay = Vec3(1f, 0f, 0f)
    val YRay = Vec3(0f, 1f, 0f)
    val ZRay = Vec3(0f, 0f, 1f)
}
