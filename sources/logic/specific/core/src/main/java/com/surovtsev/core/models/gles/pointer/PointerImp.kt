/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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

