package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import glm_.vec3.Vec3
import glm_.vec4.Vec4

interface IPointer {
    val near: Vec3
    val far: Vec3

    fun getPointerDescriptor(): PointerDescriptor
}

data class PointerDescriptor(
    val near: Vec3,
    val far: Vec3) {

    val n = far - near

    fun calcProjection(p: Vec3): Vec3 {
        val a_lambda = n.dot(n)
        val b = n.dot(near - p)
        val l = -1 * b / a_lambda
        return near + n * l
    }
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

    override fun getPointerDescriptor() = PointerDescriptor(near, far)
}
