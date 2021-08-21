package com.surovtsev.cool_3d_minesweeper.game_logic

import android.util.Log
import glm_.vec3.Vec3i

object NeighboursCalculator {
    fun fillNeighbours(gameObject: GameObject, bombsList: BombsList) {
        val descriptions = gameObject.descriptions
        val counts = gameObject.counts

        fun getRange(pos: Int, dim: Short): Pair<IntRange, IntRange> =
            IntRange(pos, pos) to IntRange(Math.max(pos - 1, 0), Math.min(pos + 1, dim - 1))

        fun fillNumbers(xRange: IntRange, yRange: IntRange, zRange: IntRange, pos: Int) {
            for (x in xRange) {
                for (y in yRange) {
                    for (z in zRange) {
                        val d = GameObject.Position.getValue(
                            descriptions, Vec3i(x, y, z))

                        if (d.isBomb) continue

                        d.neighbourBombs[pos]++
                    }
                }
            }
        }

        fun helperFunction(idx: Vec3i) {
            val xRanges = getRange(idx.x, counts.x)
            val yRanges = getRange(idx.y, counts.y)
            val zRanges = getRange(idx.z, counts.z)

            fillNumbers(xRanges.first, yRanges.second, zRanges.second, 0)
            fillNumbers(xRanges.second, yRanges.first, zRanges.second, 1)
            fillNumbers(xRanges.second, yRanges.second, zRanges.first, 2)
        }

        for (b in bombsList) {
            helperFunction(b)
        }
    }
}