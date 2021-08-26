package com.surovtsev.cool_3d_minesweeper.game_logic

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.game_logic.data.CubePosition
import com.surovtsev.cool_3d_minesweeper.game_logic.data.PointedCube
import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import glm_.vec3.Vec3s

data class GameObject(
    val counts: Vec3s,
    val bombsCount: Int
) {
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
