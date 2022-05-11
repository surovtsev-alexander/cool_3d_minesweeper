package com.surovtsev.gamestate.logic.models.game.aabb.tree.node

import com.surovtsev.core.models.game.cellpointers.ObjWithCellIndexList
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.node.helpers.SplitType
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import glm_.vec3.Vec3


typealias SelectedCells = ObjWithCellIndexList<CellSpaceBorder>

interface Node {
    companion object {
        fun createNode(
            selectedCells: SelectedCells,
            spaceCoordinates: CellSpaceBorder = calculateSpaceCoordinates(selectedCells),
            prevSplitType: SplitType = SplitType.Z,
        ): Node {

            val selectedCellsCount = selectedCells.count()

            assert(selectedCellsCount > 0)

            if (selectedCellsCount == 1) {
               return Leaf(
                   selectedCells[0]
               )
            }

            return InnerNode(
                selectedCells,
                spaceCoordinates,
                prevSplitType,
            )
        }

        private fun calculateSpaceCoordinates(
            selectedCellsWithIndexes: ObjWithCellIndexList<CellSpaceBorder>
        ): CellSpaceBorder {
            assert(selectedCellsWithIndexes.isNotEmpty())

            val firstSelectedCell = selectedCellsWithIndexes[0].first

            val leftDownNear = Vec3(firstSelectedCell.mainDiagonalPoints[0])
            val rightUpFar = Vec3(firstSelectedCell.mainDiagonalPoints[1])

            for (i in 1 until selectedCellsWithIndexes.count()) {
                val cell = selectedCellsWithIndexes[i].first

                updateLeftDownNear(
                    leftDownNear,
                    cell.mainDiagonalPoints[0]
                )

                updateRightUpFar(
                    rightUpFar,
                    cell.mainDiagonalPoints[1]
                )
            }

            return CellSpaceBorder(leftDownNear, rightUpFar)
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
}
