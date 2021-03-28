package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects

import android.util.Log

class Cubes(val triangleCoordinates: FloatArray,
            val trianglesNums: FloatArray,
            val trianglesTextures: FloatArray) {
    companion object {
        fun cubes(indexedCubes: IndexedCubes): Cubes {
            val compactCoordinates = indexedCubes.trianglesCoordinates
            val indexes = indexedCubes.indexes
            val pointsCount = indexes.count()

            val trianglesCoordinatesCount = 3 * pointsCount

            val trianglesCoordinates = FloatArray(trianglesCoordinatesCount)
            val trianglesNums = FloatArray(pointsCount)
            val trianglesTextures = FloatArray(pointsCount)

            for (i in 0 until pointsCount) {
                val pointId = indexes[i]
                val startCC = pointId * 3
                val startTC = i * 3

                for (j in 0 until 3) {
                    trianglesCoordinates[startTC + j] = compactCoordinates[startCC + j]
                }

                trianglesNums[i] = (i / 3 % 2).toFloat() + 0.1f
                trianglesTextures[i] = (i / 6 % 6).toFloat() + 0.1f
            }

            if (true) {
                var test_str = ""
                if (false) {
                    test_str += "trianglesNums:\n"
                    for (i in 0 until trianglesNums.size / 3) {
                        test_str += "${trianglesNums[i * 3]}\t${trianglesNums[i * 3 + 1]}\t${trianglesNums[i * 3 + 2]}\n"
                    }
                }
                test_str += "trianglesTextures:\n"
                for (i in 0 until trianglesTextures.size / 3) {
                    test_str += "${trianglesTextures[i * 3]}\t${trianglesTextures[i * 3 + 1]}\t${trianglesTextures[i * 3 + 2]}\n"
                }
                Log.d("TEST", test_str)
            }

            return Cubes(trianglesCoordinates, trianglesNums, trianglesTextures)
        }
    }
}