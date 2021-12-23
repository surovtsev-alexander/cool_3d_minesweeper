package com.surovtsev.gamescreen.models.game.cellpointers

import com.surovtsev.gamescreen.models.game.skin.cube.cell.CellSkin

open class PointedCell(
    val index: CellIndex,
    val skin: CellSkin
) {
    override fun toString() = "${index.getVec()} $skin"
}
