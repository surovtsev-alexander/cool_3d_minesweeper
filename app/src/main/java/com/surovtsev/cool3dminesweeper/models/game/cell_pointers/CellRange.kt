package com.surovtsev.cool3dminesweeper.models.game.cell_pointers

import glm_.vec3.Vec3i

data class CellRange(
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    companion object {
        fun getIntRange(v: Int) = 0 until v
    }

    constructor(counts: Vec3i): this(
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

    fun iterate(counts: Vec3i, action: (CellIndex) -> Unit) {
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

    override fun toString() = "$xRange $yRange $zRange"
}
