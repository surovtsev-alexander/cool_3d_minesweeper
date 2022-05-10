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
) {
    val counts = settingsData.dimensions.toVec3i()

    private val bombsRate =  settingsData.bombsPercentage.toFloat() / 100f


    val space: Vec3
    val gaps: Vec3

    init {
        assert(bombsRate > 0)
        assert(bombsRate < 1)

        val dimensions = settingsData.dimensions

        val maxDim = max(max(dimensions.x, dimensions.y), dimensions.z)
        val cellDim = 5f / maxDim

        space = Vec3(counts) * cellDim
        @Suppress("ConstantConditionIf")
        gaps = if (false) space / counts / 40 else if (true) Vec3() else space / counts / 10
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
