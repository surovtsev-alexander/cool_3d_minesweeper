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

typealias My3DList<T> = List<List<List<T>>>

// Flatten version of My3DList with CellIndex for each element
typealias ObjWithCellIndexList<T> = List<Pair<T, CellIndex>>


data class Range3D(
    val counts: Vec3i,
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    companion object {
        fun getIntRange(v: Int) = 0 until v
    }

    constructor(counts: Vec3i): this(
        counts,
        getIntRange(
            counts[0]
        ),
        getIntRange(
            counts[1]
        ),
        getIntRange(
            counts[2]
        )
    )

    fun iterate(action: (cellIndex: CellIndex) -> Unit) {
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    action(
                        CellIndex(
                            x,
                            y,
                            z,
                            counts
                        )
                    )
                }
            }
        }
    }

    fun <T> create3DList(action: (cellIndex: CellIndex) -> T): My3DList<T> =
        xRange.map { x ->
            yRange.map { y ->
                zRange.map { z ->
                    action(
                        CellIndex(
                            x,
                            y,
                            z,
                            counts
                        )
                    )
                }
            }
        }

    fun <T> createObjWithCellIndexList(
        arr: My3DList<T>
    ): ObjWithCellIndexList<T> =
        create3DList { cellIndex ->
            cellIndex.getValue(arr) to cellIndex
        }.flatten().flatten()

    fun <T> toFlattenList(
        arr: My3DList<T>
    ): List<T> =
        arr.flatten().flatten()

    override fun toString() = "$xRange $yRange $zRange"
}

// TODO: Move to a separate file
fun Vec3i.cellsCount() = this.x * this.y * this.z
