package com.surovtsev.gamelogic.minesweeper.helpers

import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.cellpointers.PointedCellWithSpaceBorder
import com.surovtsev.gamestateholder.GameStateHolder
import javax.inject.Inject

@GameScope
class IntersectionCalculator @Inject constructor(
    private val pointer: Pointer,
    private val gameStateHolder: GameStateHolder,
) {

    fun getCell(): PointedCell? {
        val gameState = gameStateHolder.gameStateFlow.value ?: return null

        val cubeInfo = gameState.cubeInfo

        val cubeSpaceBorder = cubeInfo.cubeSpaceBorder
        val borders = cubeSpaceBorder.cells
        val squaredCubeSphereRadius = cubeSpaceBorder.squaredCellSphereRadius

        val pointerDescriptor = pointer.getPointerDescriptor()

        val candidateCubes =
            mutableListOf<Pair<Float, PointedCellWithSpaceBorder>>()

        cubeInfo.cubeSkin.skinsWithIndexes.forEach { (skin, cellIndex) ->
            do {
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