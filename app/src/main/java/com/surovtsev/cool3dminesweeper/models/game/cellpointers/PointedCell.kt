package com.surovtsev.cool3dminesweeper.models.game.cellpointers

import com.surovtsev.cool3dminesweeper.models.game.skin.cube.cell.CellSkin

open class PointedCell(
    val index: CellIndex,
    val skin: CellSkin
) {
    override fun toString() = "${index.getVec()} $skin"
}
