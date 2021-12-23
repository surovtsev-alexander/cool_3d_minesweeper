package com.surovtsev.gamescreen.models.game.spaceborders.cube

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.gamescreen.models.game.spaceborders.cube.cell.CellSpaceBorder
import com.surovtsev.gamescreen.models.game.cellpointers.CellIndex
import com.surovtsev.gamescreen.models.game.config.GameConfig
import javax.inject.Inject

@GameScope
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

