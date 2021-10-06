package com.surovtsev.cool3dminesweeper.models.game.cell_pointers

import glm_.vec3.Vec3i

@Suppress("EqualsOrHashCode")
class CellIndex(
    val x: Int,
    val y: Int,
    val z: Int,
    counts: Vec3i
) {
    val id =
        calcId(
            counts,
            x,
            y,
            z
        )

    companion object {
        fun calcId(counts: Vec3i, x: Int, y: Int, z: Int) =
            x + counts[0] * (y + counts[1] * z)

        fun getIndexCalculator(counts: Vec3i): (Int) -> CellIndex =  { xyz ->
            val countX = counts[0]
            val countY = counts[1]
            val x = xyz % countX
            val yz = (xyz - x) / countX
            val y = yz % countY
            val z = (yz - y) / countY
            CellIndex(
                x,
                y,
                z,
                counts
            )
        }

        fun <T> getValue(arr: Array<Array<Array<T>>>, pos: Vec3i) =
            arr[pos[0]][pos[1]][pos[2]]

    }

    constructor(v: Vec3i, counts: Vec3i): this(
        v[0], v[1], v[2], counts
    )

    override fun equals(other: Any?): Boolean {
        return if (other is CellIndex) {
            (x == other.x && y == other.y && z == other.z && id == other.id)
        } else {
            super.equals(other)
        }
    }

    fun getVec() = Vec3i(x, y, z)

    fun <T> getValue(arr: Array<Array<Array<T>>>): T = arr[x][y][z]

    override fun toString() = getVec().toString()
}