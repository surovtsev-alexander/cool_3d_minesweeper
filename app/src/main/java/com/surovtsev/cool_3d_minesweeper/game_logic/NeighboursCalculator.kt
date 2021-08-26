package com.surovtsev.cool_3d_minesweeper.game_logic

import com.surovtsev.cool_3d_minesweeper.game_logic.data.PointedCube
import glm_.vec3.Vec3bool
import glm_.vec3.Vec3s


typealias PairDimRange = Pair<IntRange, IntRange>

object NeighboursCalculator {

    fun getPairDimRange(pos: Int, dim: Short): PairDimRange =
        IntRange(pos, pos) to IntRange(Math.max(pos - 1, 0), Math.min(pos + 1, dim - 1))

    data class DimRanges(
        val xRange: IntRange,
        val yRange: IntRange,
        val zRange: IntRange
    ) {
        fun iterate(counts: Vec3s, action: (GameObject.Position) -> Unit) {
            for (x in xRange) {
                for (y in yRange) {
                    for (z in zRange) {
                        action(GameObject.Position(x, y, z, counts))
                    }
                }
            }
        }
    }

    data class PairDimRanges(
        val xRange: PairDimRange,
        val yRange: PairDimRange,
        val zRange: PairDimRange
    ) {
        constructor(idx: GameObject.Position, counts: Vec3s): this(
            getPairDimRange(idx.x, counts.x),
            getPairDimRange(idx.y, counts.y),
            getPairDimRange(idx.z, counts.z)
        )

        companion object {
            fun selectRange(pair: PairDimRange, first: Boolean) = if (first) pair.first else pair.second
        }

        fun getDimRanges(flags: Vec3bool) =
            DimRanges(
                selectRange(xRange, flags[0]),
                selectRange(yRange, flags[1]),
                selectRange(zRange, flags[2])
            )
    }


    fun iterateAllNeighbours(
        gameObject: GameObject, xyz: GameObject.Position,
        action: (PointedCube) -> Unit
    ) {
        val ranges = PairDimRanges(xyz, gameObject.counts).getDimRanges(
            Vec3bool(false, false, false)
        )

        iterate(gameObject, xyz, ranges, { c, i -> action(c) }, 0)
    }

    fun iterate(
        gameObject: GameObject, xyz: GameObject.Position,
        ranges: DimRanges,
        action: (PointedCube, Int) -> Unit, i: Int
    ) {
        val counts = gameObject.counts

        ranges.iterate(counts) {
            do {
                if (it == xyz) {
                    break
                }

                val c = gameObject.getPointedCube(it)
                val d = c.description

                if (d.isBomb || d.isEmpty()) break

                action(c, i)
            } while (false)
        }
    }

    fun iterateNeightbours(
        gameObject: GameObject, xyz: GameObject.Position,
        action: (PointedCube, Int) -> Unit
    ) {
        val ranges = PairDimRanges(xyz, gameObject.counts)

        iterate(gameObject, xyz, ranges.getDimRanges(Vec3bool(true, false, false)), action, 0)
        iterate(gameObject, xyz, ranges.getDimRanges(Vec3bool(false, true, false)), action, 1)
        iterate(gameObject, xyz, ranges.getDimRanges(Vec3bool(false, false, true)), action, 2)
    }

    fun fillNeighbours(gameObject: GameObject, bombsList: BombsList) {
        for (b in bombsList) {
            iterateNeightbours(gameObject, b, { c, i -> c.description.neighbourBombs[i]++ })
        }
    }

    fun bombRemoved(gameObject: GameObject, position: GameObject.Position) {
        iterateNeightbours(gameObject,position, { c, i -> c.description.neighbourBombs[i]-- })
    }
}