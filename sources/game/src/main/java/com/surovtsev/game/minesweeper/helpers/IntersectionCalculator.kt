package com.surovtsev.game.minesweeper.helpers

import com.surovtsev.game.utils.gles.model.pointer.Pointer
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.models.game.border.cube.CubeBorder
import com.surovtsev.game.models.game.cellpointers.CellIndex
import com.surovtsev.game.models.game.cellpointers.PointedCell
import com.surovtsev.game.models.game.cellpointers.PointedCellWithBorder
import com.surovtsev.game.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class IntersectionCalculator @Inject constructor(
    private val pointer: Pointer,
    private val cubeSkin: CubeSkin,
    cubeBorder: CubeBorder
) {
    private val skins = cubeSkin.skins
    private val borders = cubeBorder.cells
    private val squaredCubeSphereRadius = cubeBorder.squaredCellSphereRadius

    fun getCell(): PointedCell? {
        val pointerDescriptor = pointer.getPointerDescriptor()

        val candidateCubes =
            mutableListOf<Pair<Float, PointedCellWithBorder>>()

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
                        fromNear to PointedCellWithBorder(
                            cellIndex, skin, spaceParameter
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