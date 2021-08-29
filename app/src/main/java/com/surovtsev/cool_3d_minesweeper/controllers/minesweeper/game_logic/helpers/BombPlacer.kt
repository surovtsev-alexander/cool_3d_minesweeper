package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers

import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import kotlin.random.Random

typealias BombsList = MutableList<CellIndex>

object BombPlacer {
    fun placeBombs(cubeSkin: CubeSkin, excludedIndex: CellIndex, bombsCount: Int): BombsList {
        val counts = cubeSkin.counts

        val allCubesCount = counts.x * counts.y * counts.z
        var freeCubes = allCubesCount - 1

        assert(freeCubes > bombsCount)

        val tryCount = 4

        val positionCalculator = CellIndex.getIndexCalculator(cubeSkin.counts)

        val skins = cubeSkin.skins

        val bombsList = mutableListOf<CellIndex>()

        fun placeBomb() {
            for (i in 0 until tryCount) {
                val rId = Random.nextInt(allCubesCount)
                val xyz = positionCalculator(rId)
                val d = xyz.getValue(skins)

                if (d.isBomb) {
                    continue
                }

                if (xyz == excludedIndex) {
                    continue
                }

                d.isBomb = true
                bombsList.add(xyz)

                freeCubes--
                return
            }

            fun tryToSetSequentially(n: Int): Boolean {
                var nn = n
                for (id in 0 until allCubesCount) {
                    val xyz = positionCalculator(id)
                    val s = xyz.getValue(skins)

                    if (s.isBomb) {
                        continue
                    }

                    if (xyz.equals(excludedIndex)) {
                        continue
                    }

                    nn--
                    if (nn == 0) {
                        s.isBomb = true
                        bombsList.add(xyz)

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