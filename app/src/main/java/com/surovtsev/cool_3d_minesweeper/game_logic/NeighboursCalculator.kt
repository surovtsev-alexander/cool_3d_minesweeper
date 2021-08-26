package com.surovtsev.cool_3d_minesweeper.game_logic

import com.surovtsev.cool_3d_minesweeper.game_logic.data.CubePosition
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
        fun iterate(counts: Vec3s, action: (CubePosition) -> Unit) {
            for (x in xRange) {
                for (y in yRange) {
                    for (z in zRange) {
                        action(CubePosition(x, y, z, counts))
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
        constructor(idx: CubePosition, counts: Vec3s): this(
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
        gameObject: GameObject, xyz: CubePosition,
        action: (PointedCube) -> Unit
    ) {
        val ranges = PairDimRanges(xyz, gameObject.counts).getDimRanges(
            Vec3bool(false, false, false)
        )

        val fl = {c: PointedCube, i: Int ->
            if (!c.description.isBomb) {
                action(c)
            }
        }
        iterate(gameObject, xyz, ranges, fl, 0)
    }

    fun iterate(
        gameObject: GameObject, xyz: CubePosition,
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

                if (d.isEmpty()) break

                action(c, i)
            } while (false)
        }
    }

    fun getNeighbours(gameObject: GameObject, xyz: CubePosition, dim: Int): List<PointedCube> {
        val res = mutableListOf<PointedCube>()

        val ranges = PairDimRanges(xyz, gameObject.counts)

        iterate(gameObject, xyz, ranges.getDimRanges(rangesFlags[dim]!!), { pointedCube, i ->
            res.add(pointedCube)
        }, dim)

        return res
    }

    private val rangesFlags = mapOf<Int, Vec3bool>(
        0 to Vec3bool(true, false, false),
        1 to Vec3bool(false, true, false),
        2 to Vec3bool(false, false, true)
    )

    fun iterateNeightbours(
        gameObject: GameObject, xyz: CubePosition,
        action: (PointedCube, Int) -> Unit
    ) {
        val ranges = PairDimRanges(xyz, gameObject.counts)

        for (r in rangesFlags) {
            iterate(gameObject, xyz, ranges.getDimRanges(r.value), action, r.key)
        }
    }

    fun fillNeighbours(gameObject: GameObject, bombsList: BombsList) {
        val fl = {c: PointedCube, i: Int ->
            if (!c.description.isBomb) {
                c.description.neighbourBombs[i]++
            }
        }

        for (b in bombsList) {
            iterateNeightbours(gameObject, b, fl)
        }
    }

    fun bombRemoved(gameObject: GameObject, position: CubePosition) {
        iterateNeightbours(gameObject,position, { c, i -> c.description.neighbourBombs[i]-- })
    }
}