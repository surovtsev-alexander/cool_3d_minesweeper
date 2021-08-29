package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameObject
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.cube.Cube
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
import java.lang.StringBuilder

class CubeCoordinates(val triangleCoordinates: FloatArray,
                      val isEmpty: FloatArray,
                      val textureCoordinates: FloatArray,
                      val gameObject: GameObject,
                      val cube: Cube,
                      val gameStatusesReceiver: IGameStatusesReceiver
) {
    companion object {
        fun cubes(cubesCoordinatesGenerator: CubesCoordinatesGenerator): CubeCoordinates {
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

            val gameObject =
                GameObject(
                    cubesCoordinatesGenerator.cube.counts,
                    cubesCoordinatesGenerator.bombsCount
                )

            val res = CubeCoordinates(
                trianglesCoordinates,
                isEmpty,
                textureCoordinates,
                gameObject,
                cubesCoordinatesGenerator.cube,
                cubesCoordinatesGenerator.gameStatusesReceiver
            )

            return res
        }
    }
}