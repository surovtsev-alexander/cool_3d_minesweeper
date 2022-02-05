package com.surovtsev.core.models.gles.pointer

import com.surovtsev.utils.androidview.interaction.TouchType
import glm_.vec3.Vec3

interface Pointer {
    val near: Vec3
    val far: Vec3
    val touchType: TouchType

    fun getPointerDescriptor(): PointerDescriptor
}

data class PointerDescriptor(
    val near: Vec3,
    val far: Vec3) {

    val n = far - near

    fun calcProjection(p: Vec3): Vec3 {
        val aLambda = n.dot(n)
        val b = n.dot(near - p)
        val l = -1 * b / aLambda
        return near + n * l
    }
}

open class PointerData(
    open var near: Vec3,
    open var far: Vec3,
    open var touchType: TouchType
)

open class PointerImp: PointerData(Vec3(), Vec3(), TouchType.SHORT),
    Pointer {
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

    override var touchType: TouchType
        get() = super.touchType
        set(value) {
            super.touchType = value
        }

    override fun getPointerDescriptor() =
        PointerDescriptor(
            near,
            far
        )
}

