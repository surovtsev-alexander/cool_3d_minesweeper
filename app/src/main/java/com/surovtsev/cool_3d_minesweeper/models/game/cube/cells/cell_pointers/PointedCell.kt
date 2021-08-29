package com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.CubeDescription

open class PointedCell(
    val position: CellPosition,
    val description: CubeDescription
) {
    override fun toString() = "${position.getVec()} $description"
}
