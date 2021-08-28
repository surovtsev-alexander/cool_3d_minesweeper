package com.surovtsev.cool_3d_minesweeper.models.game

import glm_.vec3.Vec3s

data class DimRanges(
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    companion object {
        fun getIntRange(v: Short) = 0 until v
    }

    constructor(counts: Vec3s): this(
        getIntRange(counts.x),
        getIntRange(counts.y),
        getIntRange(counts.z)
    )

    fun iterate(counts: Vec3s, action: (CubePosition) -> Unit) {
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    action(CubePosition(x, y, z, counts))
                }
            }
        }
    }

    override fun toString() = "${xRange.toString()} ${yRange.toString()} ${zRange.toString()}"
}
