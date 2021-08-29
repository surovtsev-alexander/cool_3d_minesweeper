package com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers

import glm_.vec3.Vec3i
import glm_.vec3.Vec3s

class CellIndex(
    val x: Int,
    val y: Int,
    val z: Int,
    counts: Vec3s
) {
    val id =
        calcId(
            counts,
            x,
            y,
            z
        )

    companion object {
        fun calcId(counts: Vec3s, x: Int, y: Int, z: Int) =
            x + counts.x * (y + counts.y * z)

        fun getIndexCalculator(counts: Vec3s): (Int) -> CellIndex =  { xyz ->
            val x = xyz % counts.x.toInt()
            val yz = (xyz - x) / counts.x
            val y = yz % counts.y
            val z = (yz - y) / counts.y
            CellIndex(
                x,
                y,
                z,
                counts
            )
        }

        fun <T> getValue(arr: Array<Array<Array<T>>>, pos: Vec3i) =
            arr[pos.x][pos.y][pos.z]

    }

    constructor(v: Vec3i, counts: Vec3s): this(
        v.x, v.y, v.z, counts
    )

    override fun equals(other: Any?): Boolean {
        if (other is CellIndex) {
            return (x == other.x && y == other.y && z == other.z && id == other.id)
        } else {
            return super.equals(other)
        }
    }

    fun getVec() = Vec3i(x, y, z)

    fun <T> getValue(arr: Array<Array<Array<T>>>): T = arr[x][y][z]

    override fun toString() = getVec().toString()
}