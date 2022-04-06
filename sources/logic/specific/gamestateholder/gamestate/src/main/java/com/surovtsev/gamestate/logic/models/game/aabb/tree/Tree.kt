package com.surovtsev.gamestate.logic.models.game.aabb.tree

import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Node
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.CubeSpaceBorder


typealias AABBTree = Tree

class Tree(
    val cubeSpaceBorder: CubeSpaceBorder,
) {
    val root = Node.createNode(
        cubeSpaceBorder.cellsWithIndexes
    )

    override fun toString(): String {
        return "AABBTree:\n" +
                "cellsWithIndexes:${cubeSpaceBorder.cellsWithIndexes}\n" +
                "root:\n" +
                root.toString()
    }
}
