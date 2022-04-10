package com.surovtsev.gamestate.logic.models.game.aabb.tree.node

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder

class Leaf(
    val cellSpaceBorder: CellSpaceBorder,
    val cellIndex: CellIndex,
): Node {

    constructor(
        cellWithIndex: Pair<CellSpaceBorder, CellIndex>
    ): this(cellWithIndex.first, cellWithIndex.second)

    override fun toString(): String {
        return "Leaf. CellSpaceBorder: $cellSpaceBorder; cellIndex: $cellIndex"
    }
}