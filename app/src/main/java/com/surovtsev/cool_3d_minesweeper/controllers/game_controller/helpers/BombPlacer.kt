package com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameObject
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellPosition
import kotlin.random.Random

typealias BombsList = MutableList<CellPosition>

object BombPlacer {
    fun placeBombs(gameObject: GameObject, excludedPosition: CellPosition): BombsList {
        val bombsCount = gameObject.bombsCount

        val counts = gameObject.counts

        val allCubesCount = counts.x * counts.y * counts.z
        var freeCubes = allCubesCount - 1

        assert(freeCubes > bombsCount)

        val tryCount = 4

        val positionCalculator = CellPosition.getPositionCalculator(gameObject.counts)

        val descriptions = gameObject.descriptions

        val bombsList = mutableListOf<CellPosition>()

        fun placeBomb() {
            for (i in 0 until tryCount) {
                val rId = Random.nextInt(allCubesCount)
                val xyz = positionCalculator(rId)
                val d = xyz.getValue(descriptions)

                if (d.isBomb) {
                    continue
                }

                if (xyz == excludedPosition) {
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

                    if (xyz.equals(excludedPosition)) {
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