package com.surovtsev.gamestate.logic.models.game.aabb.treealt.node

import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder

interface Node {
    val cellSpaceBorder: CellSpaceBorder
}
