package com.surovtsev.cool_3d_minesweeper.controllers.game_controller

import com.surovtsev.cool_3d_minesweeper.models.game.CubePosition
import com.surovtsev.cool_3d_minesweeper.models.game.DimRanges
import com.surovtsev.cool_3d_minesweeper.models.game.PointedCube
import glm_.vec3.Vec3s

data class GameObject(
    val counts: Vec3s,
    val bombsCount: Int
) {
    val ranges = DimRanges(counts)

    companion object {

        fun iterateCubes(counts: Vec3s, action: (xyz: CubePosition) -> Unit) {
            for (x in 0 until counts.x) {
                for (y in 0 until counts.y) {
                    for (z in 0 until counts.z) {
                        action(CubePosition(x, y, z, counts))
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

    fun getPointedCube(p: CubePosition) = PointedCube(p, p.getValue(descriptions))

    fun iterateCubes(action: (xyz: CubePosition) -> Unit) = Companion.iterateCubes(counts, action)
}