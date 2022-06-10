/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.minesweeper.helpers

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.helpers.gamelogic.TextureCoordinatesHelper

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