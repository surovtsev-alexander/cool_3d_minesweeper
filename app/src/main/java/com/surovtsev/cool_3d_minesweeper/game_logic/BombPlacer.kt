package com.surovtsev.cool_3d_minesweeper.game_logic

import glm_.vec3.Vec3i
import kotlin.random.Random

typealias BombsList = MutableList<Vec3i>

object BombPlacer {
    fun placeBombs(gameObject: GameObject, excludedPosition: GameObject.Position):BombsList {
        val bombsCount = gameObject.bombsCount

        val counts = gameObject.counts

        val allCubesCount = counts.x * counts.y * counts.z
        var freeCubes = allCubesCount - 1

        assert(freeCubes > bombsCount)

        val tryCount = 4

        val pointCalculator = GameObject.Position.getPointCalculator(gameObject.counts)

        val descriptions = gameObject.descriptions

        val bombsList = MutableList<Vec3i>(0) { Vec3i() }

        val excludedPositionVec = excludedPosition.getVec()

        fun placeBomb() {
            for (i in 0 until tryCount) {
                val rId = Random.nextInt(allCubesCount)
                val xyz = pointCalculator(rId)
                val d = GameObject.Position.getValue(descriptions, xyz)

                if (d.isBomb) {
                    continue
                }

                if (xyz == excludedPositionVec) {
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
                    val xyz = pointCalculator(id)
                    val d = GameObject.Position.getValue(descriptions, xyz)

                    if (d.isBomb) {
                        continue
                    }

                    if (xyz == excludedPositionVec) {
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