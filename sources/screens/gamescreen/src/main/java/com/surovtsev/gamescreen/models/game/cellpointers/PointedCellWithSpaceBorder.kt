package com.surovtsev.gamescreen.models.game.cellpointers

import com.surovtsev.gamescreen.models.game.spaceborders.cube.cell.CellSpaceBorder
import com.surovtsev.gamescreen.models.game.skin.cube.cell.CellSkin


class PointedCellWithSpaceBorder(
    index: CellIndex,
    skin: CellSkin,
    val spaceBorder: CellSpaceBorder
): PointedCell(index, skin)
