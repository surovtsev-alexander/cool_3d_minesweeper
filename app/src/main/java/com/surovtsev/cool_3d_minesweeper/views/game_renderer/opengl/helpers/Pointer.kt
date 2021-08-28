package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers

import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import glm_.vec3.Vec3

interface IPointer {
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
        val a_lambda = n.dot(n)
        val b = n.dot(near - p)
        val l = -1 * b / a_lambda
        return near + n * l
    }
}

open class PointerData(
    open var near: Vec3,
    open var far: Vec3,
    open var touchType: TouchType
)

class Pointer(): PointerData(Vec3(), Vec3(), TouchType.SHORT), IPointer {
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

    override fun getPointerDescriptor() = PointerDescriptor(near, far)
}
