package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.GameObject
import java.lang.StringBuilder

class Cubes(val triangleCoordinates: FloatArray,
            val trianglesNums: FloatArray,
            val trianglesTextures: FloatArray,
            val textureCoordinates: FloatArray,
            val gameObject: GameObject
) {
    companion object {
        val mTextureCoordinatesTemplate = floatArrayOf(
            0f, 1f,
            1f, 0f,
            0f, 0f,

            1f, 1f,
            1f, 0f,
            0f, 1f,
        )

        fun cubes(rawCubes: RawCubes): Cubes {
            val compactCoordinates = rawCubes.trianglesCoordinates
            val indexes = rawCubes.indexes
            val pointsCount = indexes.count()

            val trianglesCoordinatesCount = 3 * pointsCount

            val trianglesCoordinates = FloatArray(trianglesCoordinatesCount)
            val trianglesNums = FloatArray(pointsCount)
            val trianglesTextures = FloatArray(pointsCount)

            val textureCoordinates = (0 until indexes.size / 6).map {
                mTextureCoordinatesTemplate.asIterable()
            }.flatten().toFloatArray()

            for (i in 0 until pointsCount) {
                val pointId = indexes[i]
                val startCC = pointId * 3
                val startTC = i * 3

                for (j in 0 until 3) {
                    trianglesCoordinates[startTC + j] = compactCoordinates[startCC + j]
                }

                trianglesNums[i] = (i / 3 % 2).toFloat() + 0.1f

                //trianglesTextures[i] = (i / 6 % 6).toFloat() + 0.1f
                trianglesTextures[i] = 0f + 0.1f
            }

            val gameObject = GameObject(rawCubes.collisionCubes)

            val res = Cubes(
                trianglesCoordinates,
                trianglesNums,
                trianglesTextures,
                textureCoordinates,
                gameObject)

            if (false) {
                res.log()
            }

            return res
        }
    }

    fun log(): Unit {
        val test_str = StringBuilder()
        if (false) {
            test_str.append("trianglesNums:\n")
            for (i in 0 until trianglesNums.size / 3) {
                test_str.append(
                    "${trianglesNums[i * 3]}\t${trianglesNums[i * 3 + 1]}\t${trianglesNums[i * 3 + 2]}\n")
            }
        }
        if (false) {
            test_str.append("trianglesTextures:\n")
            for (i in 0 until trianglesTextures.size / 3) {
                test_str.append(
                    "${trianglesTextures[i * 3]}\t${trianglesTextures[i * 3 + 1]}\t${trianglesTextures[i * 3 + 2]}\n")
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