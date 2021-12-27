package com.surovtsev.gamelogic.models.game.cellpointers

import com.surovtsev.gamelogic.models.game.skin.cube.cell.CellSkin
import com.surovtsev.gamelogic.models.game.spaceborders.cube.cell.CellSpaceBorder


class PointedCellWithSpaceBorder(
    index: CellIndex,
    skin: CellSkin,
    val spaceBorder: CellSpaceBorder
): PointedCell(index, skin)
