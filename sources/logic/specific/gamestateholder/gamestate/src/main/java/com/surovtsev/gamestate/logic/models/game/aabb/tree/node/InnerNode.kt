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


package com.surovtsev.gamestate.logic.models.game.aabb.tree.node

import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import glm_.vec3.Vec3

class InnerNode(
    val left: Node,
    val right: Node,
): Node {
    override val cellSpaceBorder: CellSpaceBorder


    init {
        val leftDownNear = Vec3(left.cellSpaceBorder.mainDiagonalPoints[0])
        val rightUpFar = Vec3(left.cellSpaceBorder.mainDiagonalPoints[1])

        updateLeftDownNear(
            leftDownNear,
            right.cellSpaceBorder.mainDiagonalPoints[0]
        )
        updateRightUpFar(
            rightUpFar,
            right.cellSpaceBorder.mainDiagonalPoints[1]
        )

        cellSpaceBorder = CellSpaceBorder(
            leftDownNear,
            rightUpFar
        )
    }

    private fun updateLeftDownNear(
        dst: Vec3,
        x: Vec3
    ) {
        updatePoint(
            dst,
            x
        ) { a, b -> a > b }
    }

    private fun updateRightUpFar(
        dst: Vec3,
        x: Vec3,
    ) {
        updatePoint(
            dst,
            x
        ) { a, b -> a < b }
    }

    private fun updatePoint(
        dst: Vec3,
        x: Vec3,
        needToUpdate: (a: Float, b: Float) -> Boolean
    ) {
        for (i in 0 until 3) {
            val newVal = x[i]
            if (needToUpdate(dst[i], newVal)) {
                dst[i] = newVal
            }
        }
    }
}
