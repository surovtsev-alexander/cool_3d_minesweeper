package com.surovtsev.gamestate.logic.models.game.spaceborders.cube

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.My3DList
import com.surovtsev.core.models.game.cellpointers.ObjWithCellIndexList
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.logic.dagger.GameStateScope
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.CellSpaceBorder
import javax.inject.Inject

@GameStateScope
class CubeSpaceBorder @Inject constructor(
    gameConfig: GameConfig,
    cubeCoordinates: CubeCoordinates,
) {
    val cells: My3DList<CellSpaceBorder>
    val cellsWithIndexes: ObjWithCellIndexList<CellSpaceBorder>

    val squaredCellSphereRadius: Float

    init {
        val cellsRange = gameConfig.cellsRange

        val centers = cubeCoordinates.centers
        val cellSphereRadius = gameConfig.cellSphereRadius
        squaredCellSphereRadius = cellSphereRadius * cellSphereRadius

        val halfSpace = gameConfig.halfCellSpace

        cells = cellsRange.create3DList { cellIndex ->
            CellSpaceBorder(
                centers[cellIndex.id],
                halfSpace
            )
        }
        cellsWithIndexes = cellsRange
            .createObjWithCellIndexList(cells)
    }
}

