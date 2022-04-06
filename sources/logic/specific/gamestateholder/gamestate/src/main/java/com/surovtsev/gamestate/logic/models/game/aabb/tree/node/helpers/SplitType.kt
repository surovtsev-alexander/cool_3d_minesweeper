package com.surovtsev.gamestate.logic.models.game.aabb.tree.node.helpers

import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import glm_.vec3.Vec3

enum class SplitType(
    val idx: Int
) {
    X(0),
    Y(1),
    Z(2);

    fun getCyclicRangeStartsFromThis(): List<SplitType> {
        val nodeIndexes = SplitType.values()

        val selectedIndexes =
            (ordinal until nodeIndexes.size) + (0 until ordinal)

        return selectedIndexes.map { nodeIndexes[it] }
    }

    fun getNext(): SplitType {
        val nodeIndexes = SplitType.values()

        val incOrdinal = ordinal + 1

        return if (incOrdinal >= nodeIndexes.size) {
            nodeIndexes[0]
        } else {
            nodeIndexes[incOrdinal]
        }
    }
}


typealias SplitSpaces = List<CellSpaceBorder>

fun CellSpaceBorder.split(
    splitType: SplitType
): SplitSpaces {
    val updateIdx = splitType.idx

    val curr0 = this.mainDiagonalPoints[0]
    val curr1 = this.mainDiagonalPoints[1]

    val a1 = Vec3(curr1)
    val b0 = Vec3(curr0)

    val middleValue = ((curr0 + curr1) / 2)[updateIdx]

    a1[updateIdx] = middleValue
    b0[updateIdx] = middleValue

    return listOf(
        CellSpaceBorder(curr0, a1),
        CellSpaceBorder(b0, curr1)
    )
}
