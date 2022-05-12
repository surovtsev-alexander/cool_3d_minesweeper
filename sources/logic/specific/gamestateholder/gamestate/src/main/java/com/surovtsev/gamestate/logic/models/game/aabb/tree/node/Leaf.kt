package com.surovtsev.gamestate.logic.models.game.aabb.tree.node

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder

class Leaf(
    val cellIndex: CellIndex,
    override val cellSpaceBorder: CellSpaceBorder,
): Node
