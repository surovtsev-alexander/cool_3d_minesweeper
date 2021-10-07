package com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers

import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.border.cube.CubeBorder
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.CellIndex
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.PointedCell
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.PointedCellWithBorder
import com.surovtsev.cool3dminesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.IPointer
import javax.inject.Inject

@GameScope
class IntersectionCalculator @Inject constructor(
    private val pointer: IPointer,
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

        cubeSkin.iterateCubes { xyz: CellIndex ->
            do {
                val skin = xyz.getValue(skins)

                if (skin.isEmpty()) {
                    continue
                }

                val spaceParameter = xyz.getValue(borders)
                val center = spaceParameter.center

                val projection = pointerDescriptor.calcProjection(center)
                val squaredDistance = (center - projection).length2()


                if (squaredDistance <= squaredCubeSphereRadius) {
                    val fromNear = (pointerDescriptor.near - projection).length()

                    candidateCubes.add(
                        fromNear to PointedCellWithBorder(
                            xyz, skin, spaceParameter
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