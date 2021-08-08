package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import glm_.vec3.Vec3
import glm_.vec4.Vec4

interface IPointer {
    val near: Vec3
    val far: Vec3

    fun getProjectionCalculator(): (Vec3) -> Vec3
}

open class PointerData(
    open var near: Vec3,
    open var far: Vec3
)

class Pointer(): PointerData(Vec3(), Vec3()), IPointer {
    override var near: Vec3
        get() = super.near
        set(value) {
            super.near = value
        }

    override var far: Vec3
        get() = super.far
        set(value) {
            super.far = value
        }

    override fun getProjectionCalculator(): (Vec3) -> Vec3 {
        val n = far - near

        return {m:Vec3 ->
            val a_lambda = n.dot(n)
            val b = n.dot(near - m)
            val l = -1 * a_lambda / b
            val res = near + n * l
            res
        }
    }
}
