package com.surovtsev.cool_3d_minesweeper.models.game.cube

import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellPosition
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.CubeCell
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

class Cube(
    val counts: Vec3s,
    val cubeSphereRadius: Float,
    centers: Array<Vec3>,
    halfSpace: Vec3
) {
    val cells: Array<Array<Array<CubeCell>>>

    val squaredCubeSphereRadius = cubeSphereRadius * cubeSphereRadius

    init {
        cells = Array(counts.x.toInt()) { x ->
            Array(counts.y.toInt()) { y ->
                Array(counts.z.toInt()) { z ->
                    CubeCell(
                        centers[CellPosition.calcId(counts, x, y, z)],
                        halfSpace
                    )
                }
            }
        }
    }
}

