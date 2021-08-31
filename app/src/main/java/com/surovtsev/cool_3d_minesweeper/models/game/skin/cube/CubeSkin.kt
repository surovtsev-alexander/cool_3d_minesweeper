package com.surovtsev.cool_3d_minesweeper.models.game.skin.cube

import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.cell.CellSkin
import glm_.vec3.Vec3s

class CubeSkin(
    gameConfig: GameConfig
) {
    val counts = gameConfig.counts

    val cellRange =
        CellRange(
            counts
        )

    val cellCount = counts.x * counts.y * counts.z

    companion object {

        fun iterateCubes(counts: Vec3s, action: (xyz: CellIndex) -> Unit) {
            for (x in 0 until counts.x) {
                for (y in 0 until counts.y) {
                    for (z in 0 until counts.z) {
                        action(
                            CellIndex(
                                x,
                                y,
                                z,
                                counts
                            )
                        )
                    }
                }
            }
        }
    }

    val skins: Array<Array<Array<CellSkin>>> =
        Array(counts.x.toInt()) {
            Array(counts.y.toInt()) {
                Array(counts.z.toInt()) {
                    CellSkin()
                }
            }
        }

    fun getPointedCell(p: CellIndex) =
        PointedCell(
            p,
            p.getValue(skins)
        )

    fun iterateCubes(action: (xyz: CellIndex) -> Unit) =
        iterateCubes(
            counts,
            action
        )
}
