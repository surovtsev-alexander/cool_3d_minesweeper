package com.surovtsev.gamestate.models.game.spaceborders.cube

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.dagger.GameStateScope
import com.surovtsev.gamestate.models.game.spaceborders.cube.cell.CellSpaceBorder
import javax.inject.Inject

@GameStateScope
class CubeSpaceBorder @Inject constructor(
    gameConfig: GameConfig,
    cubeCoordinates: CubeCoordinates,
) {
    val cells: Array<Array<Array<CellSpaceBorder>>>

    val squaredCellSphereRadius: Float

    init {
        val centers = cubeCoordinates.centers
        val cellSphereRadius = gameConfig.cellSphereRadius
        squaredCellSphereRadius = cellSphereRadius * cellSphereRadius

        val halfSpace = gameConfig.halfCellSpace

        val counts = gameConfig.counts
        cells = Array(counts[0]) { x ->
            Array(counts[1]) { y ->
                Array(counts[2]) { z ->
                    CellSpaceBorder(
                        centers[CellIndex.calcId(counts, x, y, z)],
                        halfSpace
                    )
                }
            }
        }
    }
}

