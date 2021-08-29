package com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers

import com.surovtsev.cool_3d_minesweeper.models.game.cube.skin.CellSkin
import com.surovtsev.cool_3d_minesweeper.models.game.cube.border.cell.CellBorder

class PointedCellWithBorder(
    index: CellIndex,
    skin: CellSkin,
    val border: CellBorder
): PointedCell(index, skin)
