package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers

import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettings
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

object GameConfigFactory {
    fun createGameConfig(gameSettings: GameSettings): GameConfig {
        val settingsMap = gameSettings.settingsMap
        val counts = Vec3s(
            settingsMap[GameSettings.xCount]!!,
            settingsMap[GameSettings.yCount]!!,
            settingsMap[GameSettings.zCount]!!
        )

        val dimensions = Vec3(5f, 5f, 5f)
        val gaps = if (false) dimensions / counts / 40 else if (true) Vec3() else dimensions / counts / 10
        val bombsRate =  settingsMap[GameSettings.bombsPercentage]!!.toFloat() / 100f

        val res = GameConfig(
            counts,
            dimensions,
            gaps,
            bombsRate
        )

        return res
    }
}