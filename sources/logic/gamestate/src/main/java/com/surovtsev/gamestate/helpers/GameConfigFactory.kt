package com.surovtsev.gamestate.helpers

import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.models.game.config.GameConfig
import glm_.vec3.Vec3
import kotlin.math.max

object GameConfigFactory {
    fun createGameConfig(settingsData: Settings.SettingsData): GameConfig {
        val dimensions = settingsData.dimensions
        val counts = dimensions.toVec3i()

        val maxDim = max(max(dimensions.x, dimensions.y), dimensions.z)
        val cellDim = 5f / maxDim

        val space = Vec3(counts) * cellDim
        @Suppress("ConstantConditionIf")
        val gaps = if (false) space / counts / 40 else if (true) Vec3() else space / counts / 10
        val bombsRate =  settingsData.bombsPercentage.toFloat() / 100f

        return GameConfig(
            settingsData,
            counts,
            space,
            gaps,
            bombsRate
        )
    }
}