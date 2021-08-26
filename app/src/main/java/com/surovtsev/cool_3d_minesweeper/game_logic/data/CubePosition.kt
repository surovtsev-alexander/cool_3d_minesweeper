package com.surovtsev.cool_3d_minesweeper.game_logic.data

import glm_.vec3.Vec3i
import glm_.vec3.Vec3s

class CubePosition(val x: Int, val y: Int, val z: Int, counts: Vec3s) {
    val id = calcId(counts, x, y, z)

    companion object {
        fun calcId(counts: Vec3s, x: Int, y: Int, z: Int) =
            x + counts.x * (y + counts.y * z)

        fun getPositionCalculator(counts: Vec3s): (Int) -> CubePosition =  { xyz ->
            val x = xyz % counts.x.toInt()
            val yz = (xyz - x) / counts.x
            val y = yz % counts.y
            val z = (yz - y) / counts.y
            CubePosition(x, y, z, counts)
        }

        fun <T> getValue(arr: Array<Array<Array<T>>>, pos: Vec3i) =
            arr[pos.x][pos.y][pos.z]

    }

    override fun equals(other: Any?): Boolean {
        if (other is CubePosition) {
            return (x == other.x && y == other.y && z == other.z && id == other.id)
        } else {
            return super.equals(other)
        }
    }

    fun getVec() = Vec3i(x, y, z)

    fun <T> getValue(arr: Array<Array<Array<T>>>): T = arr[x][y][z]

    override fun toString() = getVec().toString()
}