package com.surovtsev.gamescreen.models.game.cellpointers

import com.surovtsev.gamescreen.models.game.border.cube.cell.CellBorder
import com.surovtsev.gamescreen.models.game.skin.cube.cell.CellSkin


class PointedCellWithBorder(
    index: CellIndex,
    skin: CellSkin,
    val border: CellBorder
): PointedCell(index, skin)
