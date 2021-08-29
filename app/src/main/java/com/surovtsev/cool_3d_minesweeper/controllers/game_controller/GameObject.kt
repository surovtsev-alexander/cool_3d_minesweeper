package com.surovtsev.cool_3d_minesweeper.controllers.game_controller

import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellPosition
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCell
import glm_.vec3.Vec3s

data class GameObject(
    val counts: Vec3s,
    val bombsCount: Int
) {
    val cellRange =
        CellRange(
            counts
        )

    companion object {

        fun iterateCubes(counts: Vec3s, action: (xyz: CellPosition) -> Unit) {
            for (x in 0 until counts.x) {
                for (y in 0 until counts.y) {
                    for (z in 0 until counts.z) {
                        action(
                            CellPosition(
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

    val descriptions: Array<Array<Array<CubeDescription>>> =
        Array(counts.x.toInt()) {
            Array(counts.y.toInt()) {
                Array(counts.z.toInt()) {
                    CubeDescription()
                }
            }
        }

    fun getPointedCube(p: CellPosition) =
        PointedCell(
            p,
            p.getValue(descriptions)
        )

    fun iterateCubes(action: (xyz: CellPosition) -> Unit) = Companion.iterateCubes(counts, action)
}
