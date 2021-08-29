package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameObject
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.cube.Cube
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
import java.lang.StringBuilder

class CubesFactory(val triangleCoordinates: FloatArray,
                   val isEmpty: FloatArray,
                   val textureCoordinates: FloatArray,
                   val gameObject: GameObject,
                   val cube: Cube,
                   val gameStatusesReceiver: IGameStatusesReceiver
) {
    companion object {
        fun cubes(cubesCoordinatesGenerator: CubesCoordinatesGenerator): CubesFactory {
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

            val res = CubesFactory(
                trianglesCoordinates,
                isEmpty,
                textureCoordinates,
                gameObject,
                cubesCoordinatesGenerator.cube,
                cubesCoordinatesGenerator.gameStatusesReceiver
            )

            if (LoggerConfig.LOG_TEXURE_HELPER) {
                val sb = StringBuilder()

                sb.append("TextureHelper\n")

                sb.append(TextureCoordinatesHelper.TextureType.values())
                sb.append('\n');

                sb.append(TextureCoordinatesHelper.commonTexturesPositions)
                sb.append('\n')

                sb.append(TextureCoordinatesHelper.numberTexturesPositions)
                sb.append('\n')

                sb.append(TextureCoordinatesHelper.texturesPositions)
                sb.append('\n')

                sb.append(TextureCoordinatesHelper.textureCoordinates.map {
                    it.key.toString() + " " + it.value.toList().toString()
                }.reduce {sum, elem -> sum + "\n" + elem})
                sb.append('\n')

                Log.d("TEST", sb.toString())
            }


            if (false) {
                res.log()
            }

            return res
        }
    }

    fun log(): Unit {
        val test_str = StringBuilder()
        if (false) {
            test_str.append("trianglesTextures:\n")
            for (i in 0 until isEmpty.size / 3) {
                test_str.append(
                    "${isEmpty[i * 3]}\t${isEmpty[i * 3 + 1]}\t${isEmpty[i * 3 + 2]}\n")
            }
        }
        if (false) {
            test_str.append("textureCoordinates:\n")
            for (i in 0 until textureCoordinates.size / 2) {
                test_str.append(
                    "${textureCoordinates[i * 2]}\t${textureCoordinates[i * 2 + 1]}\n")
            }
        }
        test_str.append("end")
        Log.d("TEST", test_str.toString())
    }
}