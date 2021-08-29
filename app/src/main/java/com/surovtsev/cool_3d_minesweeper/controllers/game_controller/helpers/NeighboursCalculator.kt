package com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers

import com.surovtsev.cool_3d_minesweeper.models.game.CubeSkin
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PairCellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import glm_.vec3.Vec3bool
import glm_.vec3.Vec3i


object NeighboursCalculator {
    fun iterateAllNeighbours(
        cubeSkin: CubeSkin, xyz: CellIndex,
        action: (PointedCell) -> Unit
    ) {
        val range = PairCellRange(
            xyz,
            cubeSkin.counts
        ).getCellRange(
            Vec3bool(false, false, false)
        )

        val fl = { c: PointedCell, i: Int ->
            if (!c.skin.isBomb) {
                action(c)
            }
        }
        iterate(
            cubeSkin,
            xyz,
            range,
            fl,
            0
        )
    }

    fun iterate(
        cubeSkin: CubeSkin, xyz: CellIndex,
        range: CellRange,
        action: (PointedCell, Int) -> Unit, i: Int
    ) {
        val counts = cubeSkin.counts

        range.iterate(counts) {
            do {
                if (it == xyz) {
                    break
                }

                val c = cubeSkin.getPointedCube(it)
                val d = c.skin

                if (d.isEmpty()) break

                action(c, i)
            } while (false)
        }
    }

    fun getNeighbours(cubeSkin: CubeSkin, xyz: CellIndex, dim: Int): List<PointedCell> {
        val res = mutableListOf<PointedCell>()

        val pairCellRange =
            PairCellRange(
                xyz,
                cubeSkin.counts
            )

        iterate(
            cubeSkin,
            xyz,
            pairCellRange.getCellRange(rangeFlags[dim]!!),
            { pointedCube, i ->
                res.add(pointedCube)
            },
            dim
        )

        return res
    }

    private val rangeFlags = arrayOf<Vec3bool>(
        Vec3bool(true, false, false),
        Vec3bool(false, true, false),
        Vec3bool(false, false, true)
    )

    fun iterateNeightbours(
        cubeSkin: CubeSkin, xyz: CellIndex,
        action: (PointedCell, Int) -> Unit
    ) {
        val pairCellRange =
            PairCellRange(
                xyz,
                cubeSkin.counts
            )

        for (i in 0 until 3) {
            iterate(
                cubeSkin,
                xyz,
                pairCellRange.getCellRange(rangeFlags[i]),
                action,
                i
            )
        }
    }

    fun fillNeighbours(cubeSkin: CubeSkin, bombsList: BombsList) {
        val fl = { c: PointedCell, i: Int ->
                c.skin.neighbourBombs[i] += 1
        }

        for (b in bombsList) {
            iterateNeightbours(
                cubeSkin,
                b,
                fl
            )
        }
    }

    fun hasPosEmptyNeighbours(
        cubeSkin: CubeSkin, xyz: CellIndex, direction: Int, sb: StringBuilder?): Boolean {
        val r = MyMath.Rays[direction]
        val xyzV = xyz.getVec()
        val counts = cubeSkin.counts

//        sb?.append(
//            "-\nhasPosNonEmptyNeighbours\nxyz $xyzV $direction $r\n"
//        )

        fun test_point(p: Vec3i): Boolean {
//            sb?.append("p $p\n")

            if (!MyMath.isPointInCounts(p, counts)) {
                return true
            }

            val d = cubeSkin.getPointedCube(
                CellIndex(
                    p,
                    counts
                )
            )

            return d.skin.isEmpty()
        }

        if (test_point(xyzV - r)) {
            return true
        }

        if (test_point(xyzV + r)) {
            return true
        }

        return false
    }

    fun bombRemoved(cubeSkin: CubeSkin, index: CellIndex) {
        iterateNeightbours(
            cubeSkin,
            index,
            { c, i -> c.skin.neighbourBombs[i]-- })
    }
}