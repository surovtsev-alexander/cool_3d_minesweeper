package com.surovtsev.cool_3d_minesweeper.models.game.config

import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import kotlin.math.ceil
import kotlin.math.max

data class GameConfig(
    val counts: Vec3i,
    val dimensions: Vec3,
    val gaps: Vec3,
    val bombsRate: Float
) {
    init {
        assert(bombsRate > 0)
        assert(bombsRate < 1)
    }

    val cubesCount = counts.x * counts.y * counts.z

    val bombsCount = max(1,
        ceil(
            (counts.x * counts.y * counts.z * bombsRate)
        ).toInt())

    val cellSpaceWithGaps = dimensions / counts
    val cellSpace = cellSpaceWithGaps - gaps
    val halfCellSpace = cellSpace / 2
    val cellSphereRadius = cellSpace.length() / 2
}
