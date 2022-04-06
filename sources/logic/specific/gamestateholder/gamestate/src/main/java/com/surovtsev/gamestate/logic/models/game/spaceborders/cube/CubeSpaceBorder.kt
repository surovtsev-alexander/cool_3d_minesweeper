package com.surovtsev.gamestate.logic.models.game.spaceborders.cube

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.logic.dagger.GameStateScope
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import javax.inject.Inject

@GameStateScope
class CubeSpaceBorder @Inject constructor(
    gameConfig: GameConfig,
    cubeCoordinates: CubeCoordinates,
) {
    val cells: List<List<List<CellSpaceBorder>>>

    val squaredCellSphereRadius: Float

    init {
        val centers = cubeCoordinates.centers
        val cellSphereRadius = gameConfig.cellSphereRadius
        squaredCellSphereRadius = cellSphereRadius * cellSphereRadius

        val halfSpace = gameConfig.halfCellSpace

        cells = gameConfig.cellsRange.create3DList { cellIndex ->
            CellSpaceBorder(
                centers[cellIndex.id],
                halfSpace
            )
        }
    }
}

