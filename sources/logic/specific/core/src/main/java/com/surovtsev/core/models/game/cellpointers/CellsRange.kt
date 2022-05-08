package com.surovtsev.core.models.game.cellpointers

import glm_.vec3.Vec3i

typealias My3DList<T> = List<List<List<T>>>

// Flatten version of My3DList with CellIndex for each element
typealias ObjWithCellIndexList<T> = List<Pair<T, CellIndex>>

// TODO: rename to Range3D
data class CellsRange(
    val counts: Vec3i,
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    companion object {
        fun getIntRange(v: Int) = 0 until v
    }

    constructor(counts: Vec3i): this(
        counts,
        getIntRange(
            counts[0]
        ),
        getIntRange(
            counts[1]
        ),
        getIntRange(
            counts[2]
        )
    )

    fun iterate(action: (cellIndex: CellIndex) -> Unit) {
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
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

    fun <T> create3DList(action: (cellIndex: CellIndex) -> T): My3DList<T> =
        xRange.map { x ->
            yRange.map { y ->
                zRange.map { z ->
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

    fun <T> createObjWithCellIndexList(
        arr: My3DList<T>
    ): ObjWithCellIndexList<T> =
        create3DList { cellIndex ->
            cellIndex.getValue(arr) to cellIndex
        }.flatten().flatten()

    fun <T> toFlattenList(
        arr: My3DList<T>
    ): List<T> =
        arr.flatten().flatten()

    override fun toString() = "$xRange $yRange $zRange"
}

// TODO: Move to a separate file
fun Vec3i.cellsCount() = this.x * this.y * this.z
