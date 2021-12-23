package com.surovtsev.gamescreen.models.game.skin.cube

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.models.game.cellpointers.CellIndex
import com.surovtsev.gamescreen.models.game.cellpointers.CellRange
import com.surovtsev.gamescreen.models.game.cellpointers.PointedCell
import com.surovtsev.gamescreen.models.game.config.GameConfig
import com.surovtsev.gamescreen.models.game.skin.cube.cell.CellSkin
import glm_.vec3.Vec3i
import javax.inject.Inject

@GameScope
class CubeSkin @Inject constructor(
    gameConfig: GameConfig
) {
    val counts = gameConfig.counts

    val cellRange =
        CellRange(
            counts
        )

    val cellCount = counts[0] * counts[1] * counts[2]

    companion object {

        fun iterateCubes(counts: Vec3i, action: (cellIndex: CellIndex) -> Unit) {
            for (x in 0 until counts[0]) {
                for (y in 0 until counts[1]) {
                    for (z in 0 until counts[2]) {
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
        Array(counts[0]) {
            Array(counts[1]) {
                Array(counts[2]) {
                    CellSkin()
                }
            }
        }

    fun getPointedCell(p: CellIndex) =
        PointedCell(
            p,
            p.getValue(skins)
        )

    fun iterateCubes(action: (cellIndex: CellIndex) -> Unit) =
        iterateCubes(
            counts,
            action
        )
}
