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


package com.surovtsev.core.models.game.cellpointers

import glm_.vec3.Vec3i

@Suppress("EqualsOrHashCode")
class CellIndex(
    val x: Int,
    val y: Int,
    val z: Int,
    counts: Vec3i
) {
    val id =
        calcId(
            counts,
            x,
            y,
            z
        )

    companion object {
        fun calcId(counts: Vec3i, x: Int, y: Int, z: Int) =
            z + counts[2] * (y + counts[1] * x)

//        fun calcIdZYX(counts: Vec3i, x: Int, y: Int, z: Int) =
//            x + counts[0] * (y + counts[1] * z)

        fun getIndexCalculator(counts: Vec3i): (Int) -> CellIndex =  { cellIndex ->
            val countY = counts[1]
            val countZ = counts[2]
            val z = cellIndex % countZ
            val yx = (cellIndex - z) / countZ
            val y = yx % countY
            val x = (yx - y) / countY
            CellIndex(
                x,
                y,
                z,
                counts
            )
        }

//        fun getIndexCalculatorZYX(counts: Vec3i): (Int) -> CellIndex =  { cellIndex ->
//            val countX = counts[0]
//            val countY = counts[1]
//            val x = cellIndex % countX
//            val yz = (cellIndex - x) / countX
//            val y = yz % countY
//            val z = (yz - y) / countY
//            CellIndex(
//                x,
//                y,
//                z,
//                counts
//            )
//        }

        fun <T> getValue(arr: List<List<List<T>>>, pos: Vec3i) =
            arr[pos[0]][pos[1]][pos[2]]

    }

    constructor(v: Vec3i, counts: Vec3i): this(
        v[0], v[1], v[2], counts
    )

    override fun equals(other: Any?): Boolean {
        return if (other is CellIndex) {
            (x == other.x && y == other.y && z == other.z && id == other.id)
        } else {
            super.equals(other)
        }
    }

    fun getVec() = Vec3i(x, y, z)

    fun <T> getValue(arr: List<List<List<T>>>): T = arr[x][y][z]

    override fun toString() = getVec().toString()
}