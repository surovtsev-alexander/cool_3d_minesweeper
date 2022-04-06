package com.surovtsev.core.helpers.gamelogic

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.CellsRange
import com.surovtsev.core.models.game.cellpointers.PairCellRange
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.utils.math.MyMath
import glm_.vec3.Vec3bool
import glm_.vec3.Vec3i

class NeighboursCalculator(
    private val gameConfig: GameConfig,
    private val cubeSkin: CubeSkin,
) {
    @Suppress("unused")
    fun iterateAllNeighbours(
        cellIndex: CellIndex,
        action: (PointedCell) -> Unit
    ) {
        val range = PairCellRange(
            cellIndex,
            gameConfig.counts
        ).getCellRange(
            Vec3bool(x = false, y = false, z = false)
        )

        val fl = { c: PointedCell, _: Int ->
            if (!c.skin.isBomb) {
                action(c)
            }
        }
        iterate(
            cellIndex,
            range,
            fl,
            0
        )
    }

    private fun iterate(
        cellIndex: CellIndex,
        range: CellsRange,
        action: (PointedCell, Int) -> Unit, i: Int
    ) {
        val cellIndexVec = cellIndex.getVec()
        range.iterate {
            do {
                if (it.getVec() == cellIndexVec) {
                    break
                }

                val c = cubeSkin.getPointedCell(it)
                val s = c.skin

                if (s.isEmpty()) break

                action(c, i)
            } while (false)
        }
    }

    fun getNeighbours(
        cellIndex: CellIndex,
        dim: Int
    ): List<PointedCell> {
        val res = mutableListOf<PointedCell>()

        val pairCellRange =
            PairCellRange(
                cellIndex,
                gameConfig.counts
            )

        iterate(
            cellIndex,
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
        cellIndex: CellIndex,
        action: (PointedCell, Int) -> Unit
    ) {
        val pairCellRange =
            PairCellRange(
                cellIndex,
                gameConfig.counts
            )

        for (i in 0 until 3) {
            iterate(
                cellIndex,
                pairCellRange.getCellRange(rangeFlags[i]),
                action,
                i
            )
        }
    }

    fun fillNeighbours(
        bombsList: List<CellIndex>
    ) {
        val fl = { c: PointedCell, i: Int ->
                c.skin.neighbourBombs[i] += 1
        }

        for (b in bombsList) {
            val pointedCell = cubeSkin.getPointedCell(b)
            if (pointedCell.skin.isEmpty()) {
                continue
            }
            iterateNeighbours(
                b,
                fl
            )
        }
    }

    fun hasPosEmptyNeighbours(
        cellIndex: CellIndex,
        direction: Int
    ): Boolean {
        val r = MyMath.Rays[direction]
        val cellIndexVec = cellIndex.getVec()
        val counts = gameConfig.counts

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

        if (testPoint(cellIndexVec - r)) {
            return true
        }

        if (testPoint(cellIndexVec + r)) {
            return true
        }

        return false
    }

    @Suppress("unused")
    fun bombRemoved(
        index: CellIndex
    ) {
        iterateNeighbours(index) { c, i -> c.skin.neighbourBombs[i]-- }
    }
}