/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
