package com.surovtsev.gamestate.logic.models.game.aabb.treealt.node

import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import glm_.vec3.Vec3

class InnerNode(
    val left: Node,
    val right: Node,
): Node {
    override val cellSpaceBorder: CellSpaceBorder


    init {
        val leftDownNear = Vec3(left.cellSpaceBorder.mainDiagonalPoints[0])
        val rightUpFar = Vec3(left.cellSpaceBorder.mainDiagonalPoints[1])

        updateLeftDownNear(
            leftDownNear,
            right.cellSpaceBorder.mainDiagonalPoints[0]
        )
        updateRightUpFar(
            rightUpFar,
            right.cellSpaceBorder.mainDiagonalPoints[1]
        )

        cellSpaceBorder = CellSpaceBorder(
            leftDownNear,
            rightUpFar
        )
    }

    private fun updateLeftDownNear(
        dst: Vec3,
        x: Vec3
    ) {
        updatePoint(
            dst,
            x
        ) { a, b -> a > b }
    }

    private fun updateRightUpFar(
        dst: Vec3,
        x: Vec3,
    ) {
        updatePoint(
            dst,
            x
        ) { a, b -> a < b }
    }

    private fun updatePoint(
        dst: Vec3,
        x: Vec3,
        needToUpdate: (a: Float, b: Float) -> Boolean
    ) {
        for (i in 0 until 3) {
            val newVal = x[i]
            if (needToUpdate(dst[i], newVal)) {
                dst[i] = newVal
            }
        }
    }
}
