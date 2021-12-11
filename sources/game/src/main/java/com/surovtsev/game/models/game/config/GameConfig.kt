package com.surovtsev.game.models.game.config

import com.surovtsev.core.room.entities.Settings
import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

data class GameConfig(
    val settingsData: Settings.SettingsData,
    val counts: Vec3i,
    val space: Vec3,
    val gaps: Vec3,
    val bombsRate: Float
) {
    init {
        assert(bombsRate > 0)
        assert(bombsRate < 1)
    }

    val cubesCount = counts[0] * counts[1] * counts[2]

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

    val cellSpaceWithGaps = space / counts
    val cellSpace = cellSpaceWithGaps - gaps
    val halfCellSpace = cellSpace / 2
    val cellSphereRadius = cellSpace.length() / 2
}
