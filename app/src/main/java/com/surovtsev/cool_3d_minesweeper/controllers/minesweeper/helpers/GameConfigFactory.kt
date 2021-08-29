package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers

import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

object GameConfigFactory {
    fun createGameConfig(): GameConfig {
        val d: Short = if (true) {
            12
        } else {
            7
        }

        val xDim = d
        val yDim = d
        val zDim = d

        val counts = Vec3s(xDim, yDim, zDim)

        val dimensions = Vec3(5f, 5f, 5f)
        val gaps = if (false) dimensions / counts / 40 else if (true) Vec3() else dimensions / counts / 10
        val bombsRate =  if (true)  {
            0.2f
        } else {
            0.1f
        }

        val res = GameConfig(
            counts,
            dimensions,
            gaps,
            bombsRate
        )

        return res
    }
}