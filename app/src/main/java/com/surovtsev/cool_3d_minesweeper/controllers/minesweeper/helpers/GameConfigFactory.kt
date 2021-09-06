package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import kotlin.math.max

object GameConfigFactory {
    fun createGameConfig(settingsData: SettingsData): GameConfig {
        val counts = settingsData.getCounts()

        val maxDim = max(max(counts.x, counts.y), counts.z)
        val cellDim = 5f / maxDim

        val dimensions = Vec3(counts) * cellDim
        val gaps = if (false) dimensions / counts / 40 else if (true) Vec3() else dimensions / counts / 10
        val bombsRate =  settingsData.bombsPercentage.toFloat() / 100f

        val res = GameConfig(
            counts,
            dimensions,
            gaps,
            bombsRate
        )

        return res
    }
}