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

import glm_.vec3.Vec3bool
import glm_.vec3.Vec3i
import kotlin.math.max
import kotlin.math.min

typealias PairDimRange = Pair<IntRange, IntRange>


fun getPairDimRange(pos: Int, dim: Int): PairDimRange =
    IntRange(pos, pos) to IntRange(max(pos - 1, 0), min(pos + 1, dim - 1))


data class PairCellRange(
    private val counts: Vec3i,
    val xRange: PairDimRange,
    val yRange: PairDimRange,
    val zRange: PairDimRange
) {
    constructor(idx: CellIndex, counts: Vec3i): this(
        counts,
        getPairDimRange(
            idx.x,
            counts[0]
        ),
        getPairDimRange(
            idx.y,
            counts[1]
        ),
        getPairDimRange(
            idx.z,
            counts[2]
        )
    )

    companion object {
        fun selectRange(pair: PairDimRange, first: Boolean) = if (first) pair.first else pair.second
    }

    fun getCellRange(flags: Vec3bool) =
        Range3D(
            counts,
            selectRange(
                xRange,
                flags[0]
            ),
            selectRange(
                yRange,
                flags[1]
            ),
            selectRange(
                zRange,
                flags[2]
            )
        )
}
