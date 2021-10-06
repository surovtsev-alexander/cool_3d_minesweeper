package com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers

import com.surovtsev.cool3dminesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.CellRange
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.PairCellRange
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.PointedCell
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.CellIndex
import com.surovtsev.cool3dminesweeper.utils.math.MyMath
import glm_.vec3.Vec3bool
import glm_.vec3.Vec3i

object NeighboursCalculator {
    @Suppress("unused")
    fun iterateAllNeighbours(
        cubeSkin: CubeSkin, xyz: CellIndex,
        action: (PointedCell) -> Unit
    ) {
        val range = PairCellRange(
            xyz,
            cubeSkin.counts
        ).getCellRange(
            Vec3bool(x = false, y = false, z = false)
        )

        val fl = { c: PointedCell, _: Int ->
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

    private fun iterate(
        cubeSkin: CubeSkin, xyz: CellIndex,
        range: CellRange,
        action: (PointedCell, Int) -> Unit, i: Int
    ) {
        val counts = cubeSkin.counts

        val xyzVec = xyz.getVec()
        range.iterate(counts) {
            do {
                if (it.getVec() == xyzVec) {
                    break
                }

                val c = cubeSkin.getPointedCell(it)
                val s = c.skin

                if (s.isEmpty()) break

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
            pairCellRange.getCellRange(rangeFlags[dim]),
            { pointedCell, _ ->
                res.add(pointedCell)
            },
            dim
        )

        return res
    }

    private val rangeFlags = arrayOf(
        Vec3bool(x = true, y = false, z = false),
        Vec3bool(x = false, y = true, z = false),
        Vec3bool(x = false, y = false, z = true)
    )

    fun iterateNeighbours(
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

    fun fillNeighbours(cubeSkin: CubeSkin, bombsList: List<CellIndex>) {
        val fl = { c: PointedCell, i: Int ->
                c.skin.neighbourBombs[i] += 1
        }

        for (b in bombsList) {
            val pointedCell = cubeSkin.getPointedCell(b)
            if (pointedCell.skin.isEmpty()) {
                continue
            }
            iterateNeighbours(
                cubeSkin,
                b,
                fl
            )
        }
    }

    fun hasPosEmptyNeighbours(
        cubeSkin: CubeSkin, xyz: CellIndex, direction: Int): Boolean {
        val r = MyMath.Rays[direction]
        val xyzV = xyz.getVec()
        val counts = cubeSkin.counts

        fun testPoint(p: Vec3i): Boolean {

            if (!MyMath.isPointInCounts(p, counts)) {
                return true
            }

            val s = cubeSkin.getPointedCell(
                CellIndex(
                    p,
                    counts
                )
            )

            return s.skin.isEmpty()
        }

        if (testPoint(xyzV - r)) {
            return true
        }

        if (testPoint(xyzV + r)) {
            return true
        }

        return false
    }

    @Suppress("unused")
    fun bombRemoved(cubeSkin: CubeSkin, index: CellIndex) {
        iterateNeighbours(
            cubeSkin,
            index) { c, i -> c.skin.neighbourBombs[i]-- }
    }
}