package com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers

import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.description.CellDescription
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.CubeCell

class PointedCellWithSpaceParameters(
    position: CellPosition,
    description: CellDescription,
    val cell: CubeCell
): PointedCell(position, description)
