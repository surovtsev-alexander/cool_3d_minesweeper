package com.surovtsev.gamelogic.minesweeper.helpers

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.gamelogic.models.game.cellpointers.PointedCellWithSpaceBorder
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.gamelogic.minesweeper.gamestateholder.GameStateHolder
import javax.inject.Inject

@GameScope
class IntersectionCalculator @Inject constructor(
    private val pointer: Pointer,
    private val gameStateHolder: GameStateHolder,
) {

    fun getCell(): PointedCell? {
        val gameState = gameStateHolder.gameStateFlow.value

        val cubeSkin = gameState.cubeInfo.cubeSkin
        val skins = cubeSkin.skins
        val cubeSpaceBorder = gameState.cubeSpaceBorder
        val borders = cubeSpaceBorder.cells
        val squaredCubeSphereRadius = cubeSpaceBorder.squaredCellSphereRadius

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