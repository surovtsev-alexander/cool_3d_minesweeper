package com.surovtsev.gamestate.logic.models.game.aabb.treealt

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.Range3D
import com.surovtsev.core.models.game.cellpointers.cellsCount
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.InnerNode
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.Leaf
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.Node
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.helpers.SplitType
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.CubeSpaceBorder
import glm_.vec3.Vec3i

typealias AABBTreeAlt = Tree

class Tree(
    gameConfig: GameConfig,
    cubeSpaceBorder: CubeSpaceBorder,
) {
    val root: Node

    init {
        val entireCellsRange = gameConfig.cellsRange
        val entireCellsCounts = entireCellsRange.counts

        assert(entireCellsCounts.cellsCount() >= 1)

        val leaves = cubeSpaceBorder.cellsWithIndexes.map {
            Leaf(it.second, it.first)
        }

        var currNodes = leaves as List<Node>
        var currNodesRange = entireCellsRange
        var nextStartSplitType = SplitType.X

        do {
            val nextSplit = getNextSplit(
                currNodesRange,
                nextStartSplitType
            ) ?: break

            val nextNodesRange = mergeCellsRange(
                currNodesRange,
                nextSplit
            )
            val newNodes = mergeNodes(
                currNodes,
                currNodesRange,
                nextNodesRange,
                nextSplit,
            )

            currNodes = newNodes
            currNodesRange = nextNodesRange
            nextStartSplitType = nextSplit.next()
        } while (true)

        assert(currNodes.count() == 1)

        root = currNodes[0]
    }

    companion object {
        private fun getNextSplit(
            nodesRange: Range3D,
            firstSplitCandidate: SplitType,
        ): SplitType? {
            val splits = firstSplitCandidate.cyclicRangeStartsFromThis()

            val nodesCount = nodesRange.counts

            return splits.firstOrNull {
                nodesCount[it.ordinal] > 1
            }
        }

        fun mergeCellsRange(
            currNodesRange: Range3D,
            splitType: SplitType,
        ): Range3D {
            val newCounts = Vec3i(currNodesRange.counts)

            val splitIdx = splitType.idx
            newCounts[splitIdx] = (newCounts[splitIdx] + 1) / 2

            return Range3D(
                newCounts
            )
        }

        fun mergeNodes(
            currNodes: List<Node>,
            currNodesRange: Range3D,
            newNodesRange: Range3D,
            nextSplit: SplitType,
        ): List<Node> {
            val splitIdx = nextSplit.idx

            val oldMaxLengthInSplitDirection = currNodesRange.counts[splitIdx]

            val resLength = newNodesRange.counts.cellsCount()

            val newCellIndexCalculator = CellIndex.getIndexCalculator(
                newNodesRange.counts
            )

            val currNodesCounts = currNodesRange.counts

            return (0 until resLength).map { i ->
                val newCellIndex = newCellIndexCalculator(i)

                val left = newCellIndex.getVec()
                left[splitIdx] *= 2

                val right = Vec3i(left)
                right[splitIdx] += 1

                val leftNode = currNodes[CellIndex(left, currNodesCounts).id]

                if (right[splitIdx] >= oldMaxLengthInSplitDirection) {
                    leftNode
                } else {
                    InnerNode(
                        leftNode,
                        currNodes[CellIndex(right, currNodesCounts).id]
                    )
                }
            }
        }
    }
}
