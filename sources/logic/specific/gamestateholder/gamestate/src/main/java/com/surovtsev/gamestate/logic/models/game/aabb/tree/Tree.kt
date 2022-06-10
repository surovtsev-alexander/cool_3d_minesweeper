/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamestate.logic.models.game.aabb.tree

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.Range3D
import com.surovtsev.core.models.game.cellpointers.cellsCount
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.InnerNode
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Leaf
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Node
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.helpers.SplitType
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.CubeSpaceBorder
import glm_.vec3.Vec3i

typealias AABBTree = Tree

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
