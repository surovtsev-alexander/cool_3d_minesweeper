package com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers

import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.cell.CellSkin

open class PointedCell(
    val index: CellIndex,
    val skin: CellSkin
) {
    override fun toString() = "${index.getVec()} $skin"
}
