/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
            CellSpaceBorder
                .createByCenterAndHalfSpace(
                    centers[cellIndex.id],
                    halfSpace
                )
        }
        cellsWithIndexes = cellsRange
            .createObjWithCellIndexList(cells)
    }
}

