package com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameObject
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PairCellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellPosition
import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import glm_.vec3.Vec3bool
import glm_.vec3.Vec3i


object NeighboursCalculator {
    fun iterateAllNeighbours(
        gameObject: GameObject, xyz: CellPosition,
        action: (PointedCell) -> Unit
    ) {
        val range = PairCellRange(
            xyz,
            gameObject.counts
        ).getCellRange(
            Vec3bool(false, false, false)
        )

        val fl = { c: PointedCell, i: Int ->
            if (!c.description.isBomb) {
                action(c)
            }
        }
        iterate(
            gameObject,
            xyz,
            range,
            fl,
            0
        )
    }

    fun iterate(
        gameObject: GameObject, xyz: CellPosition,
        range: CellRange,
        action: (PointedCell, Int) -> Unit, i: Int
    ) {
        val counts = gameObject.counts

        range.iterate(counts) {
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

    fun getNeighbours(gameObject: GameObject, xyz: CellPosition, dim: Int): List<PointedCell> {
        val res = mutableListOf<PointedCell>()

        val pairCellRange =
            PairCellRange(
                xyz,
                gameObject.counts
            )

        iterate(
            gameObject,
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
        gameObject: GameObject, xyz: CellPosition,
        action: (PointedCell, Int) -> Unit
    ) {
        val pairCellRange =
            PairCellRange(
                xyz,
                gameObject.counts
            )

        for (i in 0 until 3) {
            iterate(
                gameObject,
                xyz,
                pairCellRange.getCellRange(rangeFlags[i]),
                action,
                i
            )
        }
    }

    fun fillNeighbours(gameObject: GameObject, bombsList: BombsList) {
        val fl = { c: PointedCell, i: Int ->
                c.description.neighbourBombs[i] += 1
        }

        for (b in bombsList) {
            iterateNeightbours(
                gameObject,
                b,
                fl
            )
        }
    }

    fun hasPosEmptyNeighbours(
        gameObject: GameObject, xyz: CellPosition, direction: Int, sb: StringBuilder?): Boolean {
        val r = MyMath.Rays[direction]
        val xyzV = xyz.getVec()
        val counts = gameObject.counts

        sb?.append(
            "-\nhasPosNonEmptyNeighbours\nxyz $xyzV $direction $r\n"
        )

        fun test_point(p: Vec3i): Boolean {
            sb?.append("p $p\n")

            if (!MyMath.isPointInCounts(p, counts)) {
                return true
            }

            val d = gameObject.getPointedCube(
                CellPosition(
                    p,
                    counts
                )
            )

            return d.description.isEmpty()
        }

        if (test_point(xyzV - r)) {
            return true
        }

        if (test_point(xyzV + r)) {
            return true
        }

        return false
    }

    fun bombRemoved(gameObject: GameObject, position: CellPosition) {
        iterateNeightbours(
            gameObject,
            position,
            { c, i -> c.description.neighbourBombs[i]-- })
    }
}