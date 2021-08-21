package com.surovtsev.cool_3d_minesweeper.game_logic

import android.util.Log
import glm_.vec3.Vec3
import glm_.vec3.Vec3i
import glm_.vec3.Vec3s

data class GameObject(
    val counts: Vec3s,
    val bombsCount: Int
) {
    class Position(val x: Int, val y: Int, val z: Int, counts: Vec3s) {
        val id = calcId(counts, x, y, z)

        companion object {
            fun calcId(counts: Vec3s, x: Int, y: Int, z: Int) =
                x + counts.x * (y + counts.y * z)

            fun getPointCalculator(counts: Vec3s): (Int) -> Vec3i =  { xyz ->
                val x = xyz % counts.x.toInt()
                val yz = (xyz - x) / counts.x
                val y = yz % counts.y
                val z = (yz - y) / counts.y
                Vec3i(x, y, z)
            }

            fun <T> getValue(arr: Array<Array<Array<T>>>, pos: Vec3i) =
                arr[pos.x][pos.y][pos.z]

        }
        fun getVec() = Vec3i(x, y, z)

        fun <T> getValue(arr: Array<Array<Array<T>>>): T = arr[x][y][z]
    }

    companion object {

        fun iterateCubes(counts: Vec3s, action: (p: Position) -> Unit) {
            for (x in 0 until counts.x) {
                for (y in 0 until counts.y) {
                    for (z in 0 until counts.z) {
                        action(Position(x, y, z, counts))
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
}
