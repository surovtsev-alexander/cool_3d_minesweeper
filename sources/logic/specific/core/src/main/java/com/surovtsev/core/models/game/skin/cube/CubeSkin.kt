package com.surovtsev.core.models.game.skin.cube

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.CellsRange
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.game.skin.cube.cell.CellSkin

class CubeSkin constructor(
    cellsRange: CellsRange,
) {
    val skins: List<List<List<CellSkin>>> = cellsRange.create3DList { CellSkin() }

    fun getPointedCell(p: CellIndex) =
        PointedCell(
            p,
            p.getValue(skins)
        )
}
