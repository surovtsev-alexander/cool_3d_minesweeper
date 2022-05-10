package com.surovtsev.core.models.game.config

import com.surovtsev.core.models.game.cellpointers.Range3D
import com.surovtsev.core.models.game.cellpointers.cellsCount
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

    val cellsCount = counts.cellsCount()

    val cellsRange =
        Range3D(
            counts
        )

    val bombsCount =
        max(
            1,
            min(
                cellsCount - 2,
                ceil(
                    (cellsCount * bombsRate)
                ).toInt()
            )
        )

    val cellSpaceWithGaps = space / counts
    val cellSpace = cellSpaceWithGaps - gaps
    val halfCellSpace = cellSpace / 2
    val cellSphereRadius = cellSpace.length() / 2
}
