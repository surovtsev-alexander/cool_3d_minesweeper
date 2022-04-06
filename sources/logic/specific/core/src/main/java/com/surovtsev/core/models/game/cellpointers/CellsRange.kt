package com.surovtsev.core.models.game.cellpointers

import glm_.vec3.Vec3i

data class CellsRange(
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

    fun <T> create3DList(action: (cellIndex: CellIndex) -> T): List<List<List<T>>> =
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

    override fun toString() = "$xRange $yRange $zRange"
}
