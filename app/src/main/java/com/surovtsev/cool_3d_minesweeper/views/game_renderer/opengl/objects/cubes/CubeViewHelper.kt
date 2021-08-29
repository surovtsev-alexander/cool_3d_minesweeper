package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes

import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper

class CubeViewHelper(
    val triangleCoordinates: FloatArray,
    val isEmpty: FloatArray,
    val textureCoordinates: FloatArray
) {
    companion object {
        fun calculateCoordinates(cubesCoordinatesGenerator: CubesCoordinatesGenerator): CubeViewHelper {
            val compactCoordinates = cubesCoordinatesGenerator.trianglesCoordinates
            val indexes = cubesCoordinatesGenerator.indexes
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


            val res = CubeViewHelper(
                trianglesCoordinates,
                isEmpty,
                textureCoordinates
            )

            return res
        }
    }
}