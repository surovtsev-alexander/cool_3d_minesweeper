package com.surovtsev.cool_3d_minesweeper.game_logic.data

import glm_.vec3.Vec3s

data class DimRanges(
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    fun iterate(counts: Vec3s, action: (CubePosition) -> Unit) {
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    action(CubePosition(x, y, z, counts))
                }
            }
        }
    }
}
