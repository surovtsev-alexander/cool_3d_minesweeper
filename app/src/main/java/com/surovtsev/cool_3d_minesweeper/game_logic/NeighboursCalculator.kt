package com.surovtsev.cool_3d_minesweeper.game_logic

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.game_logic.data.*
import com.surovtsev.cool_3d_minesweeper.math.MyMath
import glm_.vec3.Vec3
import glm_.vec3.Vec3bool
import glm_.vec3.Vec3i
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

    private val rangesFlags = arrayOf<Vec3bool>(
        Vec3bool(true, false, false),
        Vec3bool(false, true, false),
        Vec3bool(false, false, true)
    )

    fun iterateNeightbours(
        gameObject: GameObject, xyz: CubePosition,
        action: (PointedCube, Int) -> Unit
    ) {
        val ranges = PairDimRanges(xyz, gameObject.counts)

        for (i in 0 until 3) {
            iterate(gameObject, xyz, ranges.getDimRanges(rangesFlags[i]), action, i)
        }
    }

    fun fillNeighbours(gameObject: GameObject, bombsList: BombsList) {
        val fl = {c: PointedCube, i: Int ->
                c.description.neighbourBombs[i] += 1
        }

        for (b in bombsList) {
            iterateNeightbours(gameObject, b, fl)
        }
    }

    fun hasPosEmptyNeighbours(
        gameObject: GameObject, xyz: CubePosition, direction: Int): Boolean {
        val r = MyMath.Rays[direction]
        val xyzV = xyz.getVec()
        val counts = gameObject.counts

//        val sb = StringBuilder()
//        sb.append(
//            "-\nhasPosNonEmptyNeighbours\nxyz $xyzV $r"
//        )

        fun test_point(p: Vec3i): Boolean {
//            sb.append("p $p")

            if (!MyMath.isPointInCounts(p, counts)) {
                return true
            }

            val d = gameObject.getPointedCube(
                CubePosition(p, counts)
            )

            return d.description.isEmpty()
        }

        if (test_point(xyzV - r)) {
            return true
        }

        if (test_point(xyzV + r)) {
            return true
        }

//        Log.d("TEST++", sb.toString())

        return false
    }

    fun bombRemoved(gameObject: GameObject, position: CubePosition) {
        iterateNeightbours(gameObject,position, { c, i -> c.description.neighbourBombs[i]-- })
    }
}