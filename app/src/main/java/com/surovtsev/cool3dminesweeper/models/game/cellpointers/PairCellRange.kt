package com.surovtsev.cool3dminesweeper.models.game.cellpointers

import glm_.vec3.Vec3bool
import glm_.vec3.Vec3i
import kotlin.math.max
import kotlin.math.min

typealias PairDimRange = Pair<IntRange, IntRange>


fun getPairDimRange(pos: Int, dim: Int): PairDimRange =
    IntRange(pos, pos) to IntRange(max(pos - 1, 0), min(pos + 1, dim - 1))


data class PairCellRange(
    val xRange: PairDimRange,
    val yRange: PairDimRange,
    val zRange: PairDimRange
) {
    constructor(idx: CellIndex, counts: Vec3i): this(
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
        CellRange(
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
