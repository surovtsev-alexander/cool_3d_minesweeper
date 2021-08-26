package com.surovtsev.cool_3d_minesweeper.game_logic

import com.surovtsev.cool_3d_minesweeper.game_logic.data.PointedCube
import glm_.vec3.Vec3i

object NeighboursCalculator {

    fun getRange(pos: Int, dim: Short): Pair<IntRange, IntRange> =
        IntRange(pos, pos) to IntRange(Math.max(pos - 1, 0), Math.min(pos + 1, dim - 1))

    fun iterateNeightbours(gameObject: GameObject, idx: Vec3i,
                           action: (PointedCube, Int) -> Unit) {
        val counts = gameObject.counts

        val xRanges = getRange(idx.x, counts.x)
        val yRanges = getRange(idx.y, counts.y)
        val zRanges = getRange(idx.z, counts.z)


        val descriptions = gameObject.descriptions

        fun iterate(xRange: IntRange, yRange: IntRange, zRange: IntRange,
                    i: Int) {
            for (x in xRange) {
                for (y in yRange) {
                    for (z in zRange) {
                        val p = GameObject.Position(x, y, z, counts)

                        val d = p.getValue(descriptions)

                        if (d.isBomb) continue

                        action(PointedCube(p, d), i)
                    }
                }
            }
        }

        iterate(xRanges.first, yRanges.second, zRanges.second, 0)
        iterate(xRanges.second, yRanges.first, zRanges.second, 1)
        iterate(xRanges.second, yRanges.second, zRanges.first, 2)
    }

    fun fillNeighbours(gameObject: GameObject, bombsList: BombsList) {
        for (b in bombsList) {
            iterateNeightbours(gameObject, b, { c, i -> c.description.neighbourBombs[i]++ })
        }
    }

    fun bombRemoved(gameObject: GameObject, position: GameObject.Position) {
        iterateNeightbours(gameObject,position.getVec(), { c, i -> c.description.neighbourBombs[i]-- })
    }
}