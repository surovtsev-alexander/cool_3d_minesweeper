package com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers

import com.surovtsev.cool_3d_minesweeper.models.game.cube.skin.CellSkin

open class PointedCell(
    val index: CellIndex,
    val skin: CellSkin
) {
    override fun toString() = "${index.getVec()} $skin"
}
