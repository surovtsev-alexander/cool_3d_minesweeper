package com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers

import glm_.vec3.Vec3bool
import glm_.vec3.Vec3s

typealias PairDimRange = Pair<IntRange, IntRange>


fun getPairDimRange(pos: Int, dim: Short): PairDimRange =
    IntRange(pos, pos) to IntRange(Math.max(pos - 1, 0), Math.min(pos + 1, dim - 1))


data class PairCellRange(
    val xRange: PairDimRange,
    val yRange: PairDimRange,
    val zRange: PairDimRange
) {
    constructor(idx: CellIndex, counts: Vec3s): this(
        getPairDimRange(
            idx.x,
            counts.x
        ),
        getPairDimRange(
            idx.y,
            counts.y
        ),
        getPairDimRange(
            idx.z,
            counts.z
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
