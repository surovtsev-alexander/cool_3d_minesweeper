package com.surovtsev.cool_3d_minesweeper.game_logic

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.game_logic.data.PointedCube
import glm_.vec3.Vec3s


typealias DimRanges = Pair<IntRange, IntRange>

object NeighboursCalculator {

    fun getDimRanges(pos: Int, dim: Short): DimRanges =
        IntRange(pos, pos) to IntRange(Math.max(pos - 1, 0), Math.min(pos + 1, dim - 1))

    data class Ranges(
        val xRange: DimRanges,
        val yRange: DimRanges,
        val zRange: DimRanges
    ) {
        constructor(idx: GameObject.Position, counts: Vec3s): this(
            getDimRanges(idx.x, counts.x),
            getDimRanges(idx.y, counts.y),
            getDimRanges(idx.z, counts.z)
        )
    }

    fun iterateAllNeighbours(
        gameObject: GameObject, idx: GameObject.Position,
        action: (PointedCube) -> Unit
    ) {
        val ranges = Ranges(idx, gameObject.counts)
        val (xRanges, yRanges, zRanges) = ranges

        val counts = gameObject.counts
        for (x in xRanges.second) {
            for (y in yRanges.second) {
                for (z in zRanges.second) {
                    val p = GameObject.Position(x, y, z, counts)
                    if (p == idx) {
                        continue
                    }

                    val c = gameObject.getPointedCube(p)
                    val d = c.description
                    if (d.isBomb || d.isEmpty()) continue

                    action(c)
                }
            }
        }
    }

    fun iterate(
        gameObject: GameObject,
        xRange: IntRange, yRange: IntRange, zRange: IntRange,
        action: (PointedCube, Int) -> Unit, i: Int
    ) {
        val counts = gameObject.counts
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    val p = GameObject.Position(x, y, z, counts)

                    val c = gameObject.getPointedCube(p)
                    val d = c.description

                    if (d.isBomb || d.isEmpty()) continue

                    action(c, i)
                }
            }
        }
    }

    fun iterateNeightbours(
        gameObject: GameObject, idx: GameObject.Position,
        action: (PointedCube, Int) -> Unit
    ) {
        val ranges = Ranges(idx, gameObject.counts)
        val (xRanges, yRanges, zRanges) = ranges

        iterate(gameObject, xRanges.first, yRanges.second, zRanges.second, action, 0)
        iterate(gameObject, xRanges.second, yRanges.first, zRanges.second, action, 1)
        iterate(gameObject, xRanges.second, yRanges.second, zRanges.first, action, 2)
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