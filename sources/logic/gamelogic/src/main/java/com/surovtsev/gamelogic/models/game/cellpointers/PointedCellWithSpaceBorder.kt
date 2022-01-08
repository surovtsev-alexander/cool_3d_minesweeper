package com.surovtsev.gamelogic.models.game.cellpointers

import com.surovtsev.gamestate.models.game.skin.cube.cell.CellSkin
import com.surovtsev.gamelogic.models.game.spaceborders.cube.cell.CellSpaceBorder
import com.surovtsev.gamestate.models.game.cellpointers.CellIndex
import com.surovtsev.gamestate.models.game.cellpointers.PointedCell


class PointedCellWithSpaceBorder(
    index: CellIndex,
    skin: CellSkin,
    val spaceBorder: CellSpaceBorder
): PointedCell(index, skin)
