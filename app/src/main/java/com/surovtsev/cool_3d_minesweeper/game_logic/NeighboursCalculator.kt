package com.surovtsev.cool_3d_minesweeper.game_logic

import com.surovtsev.cool_3d_minesweeper.game_logic.data.*
import glm_.vec3.Vec3bool
import glm_.vec3.Vec3s



object NeighboursCalculator {
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