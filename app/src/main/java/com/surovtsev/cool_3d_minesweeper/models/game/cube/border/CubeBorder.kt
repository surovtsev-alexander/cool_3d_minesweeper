package com.surovtsev.cool_3d_minesweeper.models.game.cube.border

import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.cube.border.cell.CellBorder
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellIndex
import glm_.vec3.Vec3

class CubeBorder(
    gameConfig: GameConfig,
    centers: Array<Vec3>,
) {
    val cells: Array<Array<Array<CellBorder>>>

    val squaredCellSphereRadius: Float

    init {
        val cellSphereRadius = gameConfig.cellSphereRadius
        squaredCellSphereRadius = cellSphereRadius * cellSphereRadius

        val halfSpace = gameConfig.halfCellSpace

        val counts = gameConfig.counts
        cells = Array(counts.x.toInt()) { x ->
            Array(counts.y.toInt()) { y ->
                Array(counts.z.toInt()) { z ->
                    CellBorder(
                        centers[CellIndex.calcId(counts, x, y, z)],
                        halfSpace
                    )
                }
            }
        }
    }
}

