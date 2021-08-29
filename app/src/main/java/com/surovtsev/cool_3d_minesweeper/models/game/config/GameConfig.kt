package com.surovtsev.cool_3d_minesweeper.models.game.config

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

data class GameConfig(
    val counts: Vec3s,
    val dimensions: Vec3,
    val gaps: Vec3,
    val bombsRate: Float
) {
    init {
        assert(bombsRate > 0)
        assert(bombsRate < 1)
    }

    val cubesCount = counts.x * counts.y * counts.z

    val bombsCount = Math.floor(
        (counts.x * counts.y * counts.z * bombsRate).toDouble()
    ).toInt()

    val cellSpaceWithGaps = dimensions / counts
    val cellSpace = cellSpaceWithGaps - gaps
    val halfCellSpace = cellSpace / 2
    val cellSphereRadius = cellSpace.length() / 2
}
