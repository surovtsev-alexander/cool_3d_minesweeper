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


package com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.edge

import com.surovtsev.core.models.gles.pointer.PointerDescriptor
import com.surovtsev.utils.math.MyMath
import glm_.glm
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.sqrt

data class EdgeSpaceBorder(
    val p1: Vec3,
    val p2: Vec3,
    val p3: Vec3,
    val p4: Vec3
) {
    private val plane: Vec4
    private val spaceArea: Float

    init {
        val (ax, ay, az) = p1
        val (bx, by, bz) = p2
        val (cx, cy, cz) = p3

        val a = (by - ay) * cz + (az - bz) * cy + ay * bz - az * by
        val b = (ax - bx) * cz + (bz - az) * cx - ax * bz + az * bx
        val c = (bx - ax) * cy + (ay - by) * cx + ax * by - ay * bx
        val d = (ay * bx - ax * by) * cz + (ax * bz - az * bx) * cy + (az * by - ay * bz) * cx

        plane = Vec4(a, b, c, d)

        spaceArea = (p1 - p2).length() * (p1 - p4).length()
    }

    companion object {
        private fun distance(p1: Vec3, p2: Vec3) = (p1 - p2).length()

        fun triangleSpaceArea(p1: Vec3, p2: Vec3, p3: Vec3): Float {
            val a =
                distance(
                    p1,
                    p2
                )
            val b =
                distance(
                    p2,
                    p3
                )
            val c =
                distance(
                    p3,
                    p1
                )

            val p = (a + b + c) / 2

            return sqrt(
                (p * (p - a) * (p - b) * (p - c)).toDouble()
            ).toFloat()
        }
    }

    private fun calcLinePlaneIntersection(pointerDescriptor: PointerDescriptor): Vec3? {
        val x1 = pointerDescriptor.near
        val n = pointerDescriptor.n

        val denominator =
            glm.dot(
                Vec4(n, 0),
                plane
            )
        val numerator =
            glm.dot(
                Vec4(x1, 1),
                plane
            )

        return if (MyMath.isZero(denominator)) {
            null
        } else {
            x1 - n * numerator / denominator
        }
    }

    private fun isIn(p: Vec3): Boolean {
        val ss = triangleSpaceArea(
            p1,
            p2,
            p
        ) + triangleSpaceArea(
            p2,
            p3,
            p
        ) + triangleSpaceArea(
            p3,
            p4,
            p
        ) + triangleSpaceArea(
            p4,
            p1,
            p
        )

        return MyMath.isZero(ss - spaceArea)
    }

    fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
        val p = calcLinePlaneIntersection(pointerDescriptor)

        return if (p == null) {
            false
        } else {
            isIn(p)
        }
    }
}
