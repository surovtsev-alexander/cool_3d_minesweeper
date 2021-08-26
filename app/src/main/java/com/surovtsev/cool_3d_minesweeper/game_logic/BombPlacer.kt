package com.surovtsev.cool_3d_minesweeper.game_logic

import kotlin.random.Random

typealias BombsList = MutableList<GameObject.Position>

object BombPlacer {
    fun placeBombs(gameObject: GameObject, excludedPosition: GameObject.Position):BombsList {
        val bombsCount = gameObject.bombsCount

        val counts = gameObject.counts

        val allCubesCount = counts.x * counts.y * counts.z
        var freeCubes = allCubesCount - 1

        assert(freeCubes > bombsCount)

        val tryCount = 4

        val positionCalculator = GameObject.Position.getPositionCalculator(gameObject.counts)

        val descriptions = gameObject.descriptions

        val bombsList = mutableListOf<GameObject.Position>()

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