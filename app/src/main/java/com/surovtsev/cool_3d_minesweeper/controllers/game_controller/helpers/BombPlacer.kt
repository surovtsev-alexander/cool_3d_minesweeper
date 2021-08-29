package com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers

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

        val descriptions = cubeSkin.skins

        val bombsList = mutableListOf<CellIndex>()

        fun placeBomb() {
            for (i in 0 until tryCount) {
                val rId = Random.nextInt(allCubesCount)
                val xyz = positionCalculator(rId)
                val d = xyz.getValue(descriptions)

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
                    val d = xyz.getValue(descriptions)

                    if (d.isBomb) {
                        continue
                    }

                    if (xyz.equals(excludedIndex)) {
                        continue
                    }

                    nn--
                    if (nn == 0) {
                        d.isBomb = true
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