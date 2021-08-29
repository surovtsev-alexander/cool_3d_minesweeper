package com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.CubeDescription
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.CubeCell

class PointedCellWithSpaceParameters(
    position: CellPosition,
    description: CubeDescription,
    val cell: CubeCell
): PointedCell(position, description)
