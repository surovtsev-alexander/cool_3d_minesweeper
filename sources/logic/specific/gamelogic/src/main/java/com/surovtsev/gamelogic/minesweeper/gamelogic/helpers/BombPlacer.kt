/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import kotlin.random.Random

typealias BombsList = MutableList<CellIndex>

object BombPlacer {
    fun placeBombs(
        gameConfig: GameConfig,
        cubeSkin: CubeSkin,
        excludedIndex: CellIndex,
        bombsCount: Int
    ): BombsList {
        val cellsCount = gameConfig.cellsCount
        var freeCubes = cellsCount - 1 // minus excludedIndex

        assert(freeCubes > bombsCount)

        val tryCount = 4

        val positionCalculator = CellIndex.getIndexCalculator(gameConfig.counts)

        val skins = cubeSkin.skins

        val bombsList = mutableListOf<CellIndex>()

        fun placeBomb() {
            for (i in 0 until tryCount) {
                val rId = Random.nextInt(cellsCount)
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
                for (id in 0 until cellsCount) {
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