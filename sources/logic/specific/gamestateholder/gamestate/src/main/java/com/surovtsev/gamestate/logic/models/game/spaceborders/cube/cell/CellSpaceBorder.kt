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


package com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell

import com.surovtsev.core.models.gles.pointer.PointerDescriptor
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.edge.EdgeSpaceBorder
import glm_.vec3.Vec3

class CellSpaceBorder(
    leftDownNearPoint: Vec3,
    rightUpFarPoint: Vec3,
    val center: Vec3 = (leftDownNearPoint + rightUpFarPoint) / 2,
) {
    override fun toString(): String {
        return "${mainDiagonalPoints[0]}; ${mainDiagonalPoints[1]}"
    }

    companion object {
        fun createByCenterAndHalfSpace(
            center: Vec3,
            halfSpace: Vec3
        ): CellSpaceBorder {
            assert(halfSpace.x > 0)
            assert(halfSpace.y > 0)
            assert(halfSpace.z > 0)

            return CellSpaceBorder(
                center - halfSpace,
                center + halfSpace,
                center,
            )
        }
    }

    val squaredSphereRadius = (center - leftDownNearPoint).length2()

    val mainDiagonalPoints: List<Vec3> = listOf(
        leftDownNearPoint,
        rightUpFarPoint
    )

    private val edgeSpaces: List<EdgeSpaceBorder>

    init {
        val (x1, y1, z1) = leftDownNearPoint
        val (x2, y2, z2) = rightUpFarPoint

        val a = Vec3(x1, y2, z1)
        val b = Vec3(x1, y2, z2)
        val c = Vec3(x2, y2, z2)
        val d = Vec3(x2, y2, z1)

        val e = Vec3(x1, y1, z1)
        val f = Vec3(x1, y1, z2)
        val g = Vec3(x2, y1, z2)
        val h = Vec3(x2, y1, z1)

        val up =
            EdgeSpaceBorder(
                a,
                b,
                c,
                d
            )
        val down =
            EdgeSpaceBorder(
                e,
                f,
                g,
                h
            )
        val left =
            EdgeSpaceBorder(
                a,
                b,
                f,
                e
            )
        val right =
            EdgeSpaceBorder(
                d,
                c,
                g,
                h
            )
        val near =
            EdgeSpaceBorder(
                a,
                d,
                h,
                e
            )
        val far =
            EdgeSpaceBorder(
                b,
                c,
                g,
                f
            )

        edgeSpaces = listOf(
            up, down, left, right, near, far
        )
    }

    fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
        return (edgeSpaces.firstOrNull { it.testIntersection(pointerDescriptor) }) != null
    }
}
