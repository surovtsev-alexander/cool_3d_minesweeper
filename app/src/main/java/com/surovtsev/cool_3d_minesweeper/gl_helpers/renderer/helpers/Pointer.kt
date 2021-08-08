package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import glm_.vec3.Vec3

interface IPointer {
    val near: Vec3
    val far: Vec3
}

open class PointerData(
    open var near: Vec3,
    open var far: Vec3
)

class Pointer(): PointerData(Vec3(), Vec3()), IPointer {
    override var near: Vec3
        get() = super.near
        set(value) { super.near = value }

    override var far: Vec3
        get() = super.far
        set(value) { super.far = value }
}
