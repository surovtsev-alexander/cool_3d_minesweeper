package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects

class Cubes(val triangleCoordinates: FloatArray,
            val trianglesInfo: IntArray) {
    companion object {
        fun cubes(indexedCubes: IndexedCubes): Cubes {
            val compactCoordinates = indexedCubes.trianglesCoordinates
            val indexes = indexedCubes.indexes
            val pointsCount = indexes.count()

            val trianglesCoordinatesCount = 3 * pointsCount
            val trianglesInfoCount = pointsCount * 2

            val trianglesCoordinates = FloatArray(trianglesCoordinatesCount)
            val trianglesInfo = IntArray(trianglesInfoCount)

            for (i in 0 until pointsCount) {
                val pointId = indexes[i]
                val startCC = pointId * 3
                val startTC = i * 3
                val startTI = i * 2

                for (j in 0 until 3) {
                    trianglesCoordinates[startTC + j] = compactCoordinates[startCC + j]
                }

                trianglesInfo[startTI] = i / 6
                trianglesInfo[startTI + 1] = i / 3 % 2
            }

            return Cubes(trianglesCoordinates, trianglesInfo)
        }
    }
}