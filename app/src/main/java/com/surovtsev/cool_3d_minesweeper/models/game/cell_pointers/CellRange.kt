package com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers

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
            counts.x
        ),
        getIntRange(
            counts.y
        ),
        getIntRange(
            counts.z
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

    override fun toString() = "${xRange.toString()} ${yRange.toString()} ${zRange.toString()}"
}
