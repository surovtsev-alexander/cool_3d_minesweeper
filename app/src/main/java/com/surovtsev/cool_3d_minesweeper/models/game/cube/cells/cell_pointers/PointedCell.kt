package com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers

import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.description.CellDescription

open class PointedCell(
    val position: CellPosition,
    val description: CellDescription
) {
    override fun toString() = "${position.getVec()} $description"
}
