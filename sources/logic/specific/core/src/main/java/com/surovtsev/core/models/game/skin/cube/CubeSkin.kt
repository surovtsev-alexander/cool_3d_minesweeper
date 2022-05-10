package com.surovtsev.core.models.game.skin.cube

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.Range3D
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.game.skin.cube.cell.CellSkin

class CubeSkin constructor(
    cellsRange: Range3D,
) {
    val skins: List<List<List<CellSkin>>> = cellsRange.create3DList { CellSkin() }

    val skinsWithIndexes = cellsRange.createObjWithCellIndexList(skins)

    fun getPointedCell(p: CellIndex) =
        PointedCell(
            p,
            p.getValue(skins)
        )
}
