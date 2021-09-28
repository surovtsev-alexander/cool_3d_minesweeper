package com.surovtsev.cool_3d_minesweeper.models.game.border.cube

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.border.cube.cell.CellBorder
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import javax.inject.Inject

@GameControllerScope
class CubeBorder @Inject constructor(
    gameConfig: GameConfig,
    cubeCoordinates: CubeCoordinates,
) {
    val cells: Array<Array<Array<CellBorder>>>

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
                    CellBorder(
                        centers[CellIndex.calcId(counts, x, y, z)],
                        halfSpace
                    )
                }
            }
        }
    }
}

