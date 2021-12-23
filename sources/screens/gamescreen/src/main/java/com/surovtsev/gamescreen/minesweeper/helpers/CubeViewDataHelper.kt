package com.surovtsev.gamescreen.minesweeper.helpers

import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.gamescreen.minesweeper.scene.texturecoordinateshelper.TextureCoordinatesHelper

class CubeViewDataHelper(
    val triangleCoordinates: FloatArray,
    val isEmpty: FloatArray,
    val textureCoordinates: FloatArray
) {
    companion object {
        fun createObject(
            cubeCoordinates: CubeCoordinates
        ): CubeViewDataHelper {
            val compactCoordinates = cubeCoordinates.trianglesCoordinates
            val indexes = cubeCoordinates.indexes
            val pointsCount = indexes.count()

            val trianglesCoordinatesCount = 3 * pointsCount

            val trianglesCoordinates = FloatArray(trianglesCoordinatesCount)
            val isEmpty = FloatArray(pointsCount)

            val tt =
                TextureCoordinatesHelper.textureCoordinates[TextureCoordinatesHelper.TextureType.CLOSED]!!.asIterable()
            val textureCoordinates = (0 until indexes.size / 6).map {
                tt
            }.flatten().toFloatArray()

            for (i in 0 until pointsCount) {
                val pointId = indexes[i]
                val startCC = pointId * 3
                val startTC = i * 3

                for (j in 0 until 3) {
                    trianglesCoordinates[startTC + j] = compactCoordinates[startCC + j]
                }

                //trianglesTextures[i] = (i / 6 % 6).toFloat() + 0.1f
                isEmpty[i] = -1f
            }

            return CubeViewDataHelper(
                trianglesCoordinates,
                isEmpty,
                textureCoordinates
            )
        }
    }
}