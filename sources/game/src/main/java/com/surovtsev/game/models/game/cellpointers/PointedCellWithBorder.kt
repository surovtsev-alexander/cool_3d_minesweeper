package com.surovtsev.game.models.game.cellpointers

import com.surovtsev.game.models.game.border.cube.cell.CellBorder
import com.surovtsev.game.models.game.skin.cube.cell.CellSkin


class PointedCellWithBorder(
    index: CellIndex,
    skin: CellSkin,
    val border: CellBorder
): PointedCell(index, skin)
