package com.surovtsev.gamestate.logic.models.game.aabb.tree.node

import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.helpers.SplitSpaces
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.helpers.SplitType
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.helpers.split
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import glm_.vec3.Vec3

typealias Nodes = List<Node>
typealias Children = Triple<Nodes, SplitType, CellSpaceBorder>
typealias Split = Pair<SelectedCells, CellSpaceBorder>
typealias Splits = List<Split>

class InnerNode(
    selectedCells: SelectedCells,
    spaceCoordinates: CellSpaceBorder,
    prevSplitType: SplitType,
) : Node {

    val children: Children

    override fun toString(): String {
        return "<<InnerNode:\n" +
                "splitType: ${children.second}\n" +
                "spaceCoordinates: ${children.third}\n" +
                children.first.joinToString("\n") { it.toString() } + "\n" +
                ">>"
    }

    init {
        val firstCurrNodeSplit = prevSplitType.next()
        val nodeSplits = firstCurrNodeSplit.cyclicRangeStartsFromThis()

        val splitsWithType = nodeSplits
            .asSequence()
            .map {
                splitCells(
                    selectedCells,
                    spaceCoordinates,
                    it
                ) to it
            }
            .filter { isValidSplit(it.first) }
            .firstOrNull()

        children = splitsWithType?.let{ (splits, splitType) ->
            Triple(
                splits.map {
                    Node.createNode(
                        it.first,
                        it.second,
                        prevSplitType = splitType
                    )
                },
                splitType,
                spaceCoordinates
            )
        } ?: Triple(createLeavesForAllCells(selectedCells, firstCurrNodeSplit), firstCurrNodeSplit, spaceCoordinates)
    }

    private fun splitCells(
        selectedCells: SelectedCells,
        spaceCoordinates: CellSpaceBorder,
        splitType: SplitType,
    ): Splits {
        val splitSpaces = spaceCoordinates.split(splitType)

        return selectedCells.split(splitSpaces)
    }

    private fun isValidSplit(
        splits: Splits
    ): Boolean {
        return splits
            .map { (selectedCells, _) ->
                selectedCells.size
            }
            .filter { it <= 0 }
            .count() <= 0
    }

    private fun createLeavesForAllCells(
        selectedCells: SelectedCells,
        firstCurrSplitType: SplitType,
    ): List<Node> {
        return selectedCells.map {
            Node.createNode(
                listOf(it),
                it.first,
                prevSplitType = firstCurrSplitType
            )
        }
    }
}

fun Float.isIn(a: Float, b: Float): Boolean {
    return (a <= this) && (this <= b)
}

val Vec3IndexesRange = 0 until 3

fun Vec3.isIn(a: Vec3, b: Vec3): Boolean {
    return Vec3IndexesRange
        .map {
            this[it].isIn(a[it], b[it])
        }
        .filter { !it }
        .count() == 0
}

fun Vec3.isIn(
    splitSpace: CellSpaceBorder
) = this.isIn(
    splitSpace.mainDiagonalPoints[0],
    splitSpace.mainDiagonalPoints[1]
)

fun CellSpaceBorder.isIntersects(
    splitSpace: CellSpaceBorder
): Boolean {
    return this.mainDiagonalPoints[0].isIn(splitSpace) || this.mainDiagonalPoints[1].isIn(splitSpace)
}

fun SelectedCells.split(
    splitSpaces: SplitSpaces
): Splits {
    return splitSpaces
        .map { splitSpace ->
            this.filter { it.first.isIntersects(splitSpace) } to splitSpace
        }
}
