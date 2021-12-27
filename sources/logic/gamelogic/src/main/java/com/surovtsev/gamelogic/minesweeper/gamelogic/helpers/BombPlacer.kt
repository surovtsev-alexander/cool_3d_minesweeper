package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.gamelogic.models.game.cellpointers.CellIndex
import com.surovtsev.gamelogic.models.game.skin.cube.CubeSkin
import kotlin.random.Random

typealias BombsList = MutableList<CellIndex>

object BombPlacer {
    fun placeBombs(cubeSkin: CubeSkin, excludedIndex: CellIndex, bombsCount: Int): BombsList {
        val counts = cubeSkin.counts

        val allCubesCount = counts[0] * counts[1] * counts[2]
        var freeCubes = allCubesCount - 1

        assert(freeCubes > bombsCount)

        val tryCount = 4

        val positionCalculator = CellIndex.getIndexCalculator(cubeSkin.counts)

        val skins = cubeSkin.skins

        val bombsList = mutableListOf<CellIndex>()

        fun placeBomb() {
            for (i in 0 until tryCount) {
                val rId = Random.nextInt(allCubesCount)
                val cellIndex = positionCalculator(rId)
                val d = cellIndex.getValue(skins)

                if (d.isBomb) {
                    continue
                }

                if (cellIndex == excludedIndex) {
                    continue
                }

                d.isBomb = true
                bombsList.add(cellIndex)

                freeCubes--
                return
            }

            fun tryToSetSequentially(n: Int): Boolean {
                var nn = n
                for (id in 0 until allCubesCount) {
                    val cellIndex = positionCalculator(id)
                    val s = cellIndex.getValue(skins)

                    if (s.isBomb) {
                        continue
                    }

                    if (cellIndex == excludedIndex) {
                        continue
                    }

                    nn--
                    if (nn == 0) {
                        s.isBomb = true
                        bombsList.add(cellIndex)

                        freeCubes--
                        return true
                    }
                }

                return false
            }

            val r = tryToSetSequentially(Random.nextInt(freeCubes))
            assert(r)
        }

        for (i in 0 until bombsCount) {
            placeBomb()
        }

        return bombsList
    }

}