package com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers

import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.cell.CellSkin
import com.surovtsev.cool_3d_minesweeper.models.game.border.cube.cell.CellBorder

class PointedCellWithBorder(
    index: CellIndex,
    skin: CellSkin,
    val border: CellBorder
): PointedCell(index, skin)
