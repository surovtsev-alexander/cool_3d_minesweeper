package com.surovtsev.cool_3d_minesweeper.models.game

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.CubeDescription

open class PointedCube(
    val position: CubePosition,
    val description: CubeDescription
) {
    override fun toString() = "${position.getVec()} $description"
}
