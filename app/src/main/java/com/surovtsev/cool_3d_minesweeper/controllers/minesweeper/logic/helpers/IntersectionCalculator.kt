package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.logic.helpers

import com.surovtsev.cool_3d_minesweeper.models.game.CubeSkin
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.models.game.cube.border.CubeBorder
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCellWithBorder
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.IPointer


class IntersectionCalculator(
    private val pointer: IPointer,
    private val cubeSkin: CubeSkin,
    cubeBorder: CubeBorder
) {
    private val skins = cubeSkin.skins
    private val borders = cubeBorder.cells
    private val squaredCubeSphereRadius = cubeBorder.squaredCellSphereRadius

    fun getIntersection(): PointedCell? {
        val pointerDescriptor = pointer.getPointerDescriptor()

        var candidateCubes =
            mutableListOf<Pair<Float, PointedCellWithBorder>>()

        cubeSkin.iterateCubes { p: CellIndex ->
            do {
                val description = p.getValue(skins)

                if (description.isEmpty()) {
                    continue
                }

                val spaceParameter = p.getValue(borders);
                val center = spaceParameter.center

                val projection = pointerDescriptor.calcProjection(center)
                val squaredDistance = (center - projection).length2()


                if (squaredDistance <= squaredCubeSphereRadius) {
                    val fromNear = (pointerDescriptor.near - projection).length()

                    candidateCubes.add(
                        fromNear to PointedCellWithBorder(
                            p, description, spaceParameter
                        )
                    )
                }
            } while (false)
        }

        val sortedCandidates = candidateCubes.sortedBy { it.first }

        for (c in sortedCandidates) {
            val candidate = c.second

            if (candidate.border.testIntersection(pointerDescriptor)) {
                return candidate
            }
        }

        return null
    }
}