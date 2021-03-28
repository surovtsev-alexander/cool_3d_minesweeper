package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects

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

                trianglesNums[i] = (i / 3 % 2).toFloat()
                trianglesTextures[i] = 0.toFloat()
            }

            return Cubes(trianglesCoordinates, trianglesNums, trianglesTextures)
        }
    }
}