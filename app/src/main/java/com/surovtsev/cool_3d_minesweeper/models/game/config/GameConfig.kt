package com.surovtsev.cool_3d_minesweeper.models.game.config

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData
import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

data class GameConfig(
    val settingsData: SettingsData,
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

    val bombsCount =
        max(
            1,
            min(
                cubesCount - 2,
                ceil(
                    (cubesCount * bombsRate)
                ).toInt()
            )
        )

    val cellSpaceWithGaps = dimensions / counts
    val cellSpace = cellSpaceWithGaps - gaps
    val halfCellSpace = cellSpace / 2
    val cellSphereRadius = cellSpace.length() / 2
}
