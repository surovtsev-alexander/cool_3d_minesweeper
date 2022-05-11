package com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.helpers

import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import glm_.vec3.Vec3

enum class SplitType(
    val idx: Int
) {
    X(0),
    Y(1),
    Z(2);

    companion object {
        private val values = values()
        private val valuesSize = values.size

        val cyclicRangeStartsFromKey =
            values().associate {
                it to it.run {
                    ((ordinal until valuesSize) + (0 until ordinal)).map { values[it] }
                }
            }

        val nextSplitTypeByKey = values.associate {
            it to values[(it.ordinal + 1).mod(valuesSize)]
        }
    }

    fun cyclicRangeStartsFromThis() =  cyclicRangeStartsFromKey[this]!!
    fun next() = nextSplitTypeByKey[this]!!
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
