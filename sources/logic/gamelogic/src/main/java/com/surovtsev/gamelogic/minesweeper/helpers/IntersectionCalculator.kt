package com.surovtsev.gamelogic.minesweeper.helpers

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.cellpointers.CellIndex
import com.surovtsev.gamelogic.models.game.cellpointers.PointedCell
import com.surovtsev.gamelogic.models.game.cellpointers.PointedCellWithSpaceBorder
import com.surovtsev.gamelogic.models.game.skin.cube.CubeSkin
import com.surovtsev.gamelogic.models.game.spaceborders.cube.CubeSpaceBorder
import com.surovtsev.gamelogic.utils.gles.model.pointer.Pointer
import javax.inject.Inject

@GameScope
class IntersectionCalculator @Inject constructor(
    private val pointer: Pointer,
    private val cubeSkin: CubeSkin,
    cubeSpaceBorder: CubeSpaceBorder
) {
    private val skins = cubeSkin.skins
    private val borders = cubeSpaceBorder.cells
    private val squaredCubeSphereRadius = cubeSpaceBorder.squaredCellSphereRadius

    fun getCell(): PointedCell? {
        val pointerDescriptor = pointer.getPointerDescriptor()

        val candidateCubes =
            mutableListOf<Pair<Float, PointedCellWithSpaceBorder>>()

        cubeSkin.iterateCubes { cellIndex: CellIndex ->
            do {
                val skin = cellIndex.getValue(skins)

                if (skin.isEmpty()) {
                    continue
                }

                val spaceParameter = cellIndex.getValue(borders)
                val center = spaceParameter.center

                val projection = pointerDescriptor.calcProjection(center)
                val squaredDistance = (center - projection).length2()


                if (squaredDistance <= squaredCubeSphereRadius) {
                    val fromNear = (pointerDescriptor.near - projection).length()

                    candidateCubes.add(
                        fromNear to PointedCellWithSpaceBorder(
                            cellIndex, skin, spaceParameter
                        )
                    )
                }
            } while (false)
        }

        val sortedCandidates = candidateCubes.sortedBy { it.first }

        for (c in sortedCandidates) {
            val candidate = c.second

            if (candidate.spaceBorder.testIntersection(pointerDescriptor)) {
                return candidate
            }
        }

        return null
    }
}