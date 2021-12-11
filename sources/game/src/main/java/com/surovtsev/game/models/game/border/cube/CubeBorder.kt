package com.surovtsev.game.models.game.border.cube

import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.game.models.game.cellpointers.CellIndex
import com.surovtsev.game.models.game.config.GameConfig
import javax.inject.Inject

@GameScope
class CubeBorder @Inject constructor(
    gameConfig: GameConfig,
    cubeCoordinates: CubeCoordinates,
) {
    val cells: Array<Array<Array<com.surovtsev.game.models.game.border.cube.cell.CellBorder>>>

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
                    com.surovtsev.game.models.game.border.cube.cell.CellBorder(
                        centers[CellIndex.calcId(counts, x, y, z)],
                        halfSpace
                    )
                }
            }
        }
    }
}

