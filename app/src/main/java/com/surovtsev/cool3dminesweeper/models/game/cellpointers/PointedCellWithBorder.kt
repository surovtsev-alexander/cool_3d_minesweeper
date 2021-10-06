package com.surovtsev.cool3dminesweeper.models.game.cellpointers

import com.surovtsev.cool3dminesweeper.models.game.skin.cube.cell.CellSkin
import com.surovtsev.cool3dminesweeper.models.game.border.cube.cell.CellBorder

class PointedCellWithBorder(
    index: CellIndex,
    skin: CellSkin,
    val border: CellBorder
): PointedCell(index, skin)
