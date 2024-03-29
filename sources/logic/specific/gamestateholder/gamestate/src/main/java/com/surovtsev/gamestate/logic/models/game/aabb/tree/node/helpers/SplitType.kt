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


package com.surovtsev.gamestate.logic.models.game.aabb.tree.node.helpers

import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import glm_.vec3.Vec3

enum class SplitType(
    val idx: Int
) {
    X(0),
    Y(1),
    Z(2);

    companion object {
        private val values = values()
        private val valuesSize = values.size

        val cyclicRangeStartsFromKey =
            values().associate {
                it to it.run {
                    ((ordinal until valuesSize) + (0 until ordinal)).map { values[it] }
                }
            }

        val nextSplitTypeByKey = values.associate {
            it to values[(it.ordinal + 1).mod(valuesSize)]
        }
    }

    fun cyclicRangeStartsFromThis() =  cyclicRangeStartsFromKey[this]!!
    fun next() = nextSplitTypeByKey[this]!!
}


typealias SplitSpaces = List<CellSpaceBorder>

fun CellSpaceBorder.split(
    splitType: SplitType
): SplitSpaces {
    val updateIdx = splitType.idx

    val curr0 = this.mainDiagonalPoints[0]
    val curr1 = this.mainDiagonalPoints[1]

    val a1 = Vec3(curr1)
    val b0 = Vec3(curr0)

    val middleValue = ((curr0 + curr1) / 2)[updateIdx]

    a1[updateIdx] = middleValue
    b0[updateIdx] = middleValue

    return listOf(
        CellSpaceBorder(curr0, a1),
        CellSpaceBorder(b0, curr1)
    )
}
