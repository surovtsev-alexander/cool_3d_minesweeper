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


package com.surovtsev.core.helpers.gamelogic

import com.surovtsev.core.models.game.config.GameConfig
import glm_.vec3.Vec3

class CubeCoordinates(
    val trianglesCoordinates: FloatArray,
    val indexes: IntArray,
    val centers: Array<Vec3>
) {
    companion object {
        private val coordinatesTemplateArray = listOf(
            -1,  1,  1, // A
            -1,  1, -1, // B
            1,  1, -1, // C
            1,  1,  1, // D
            -1, -1,  1, // E
            -1, -1, -1, // F
            1, -1, -1, // G
            1, -1,  1, // H
        )

        /*
        val indexesTemplateArray = shortArrayOf(
            1, 2, 0,
            3,
            4,
            7,
            2,
            6,
            1,
            5,
            0,
            4,
            6,
            7
        )

        val extendedIndexesTemplateArray = (0 until indexesTemplateArray.size - 2)
            .flatMap { shortArrayOf(
                indexesTemplateArray[it]
                , indexesTemplateArray[it + 1]
                , indexesTemplateArray[it + 2]).asIterable() }.toShortArray()
        */
        private val extendedIndexesTemplateArray = listOf(
            1, 2, 0, // top
            0, 2, 3,

            0, 3, 4, // near
            4, 3, 7,

            3, 2, 7, // right
            7, 2, 6,

            2, 1, 6, // far
            6, 1, 5,

            1, 0, 5, // left
            5, 0, 4,

            4, 7, 5, // down
            5, 7, 6,
        )

        val invExtendedIndexedArray = (0 until extendedIndexesTemplateArray.size / 3).map {
            intArrayOf(
                extendedIndexesTemplateArray[it * 3 + 2],
                extendedIndexesTemplateArray[it * 3 + 1],
                extendedIndexesTemplateArray[it * 3]
            ).asIterable()
        }.flatten().toIntArray()

        /*
        fun indexedCube(): RawCubes {
            val trianglesCoordinates = coordinatesTemplateArray.map { it.toFloat() }.toFloatArray()
            val indexes = extendedIndexesTemplateArray.clone()

            return RawCubes(trianglesCoordinates, indexes)
        }
        */

        fun createObject(
            gameConfig: GameConfig
        ): CubeCoordinates {
            val indexesArray =
                invExtendedIndexedArray

            val dimensions = gameConfig.space
            val gaps = gameConfig.gaps
            val cellsCount = gameConfig.cellsCount
            val cubeCoordinatesCount = coordinatesTemplateArray.size
            val cubeIndexesCount = indexesArray.size
            val coordinatesCount = cubeCoordinatesCount * cellsCount
            val indexesCount = cubeIndexesCount * cellsCount

            val trianglesCoordinates = FloatArray(coordinatesCount)
            val indexes = IntArray(indexesCount)

            val centers = Array(cellsCount) { Vec3() }

            val cubesHalfDims = dimensions / 2
            val cellSpaceWithGaps = gameConfig.cellSpaceWithGaps
            val cellWithGapsHalfSpace = cellSpaceWithGaps / 2
            val cellSpace = gameConfig.cellSpace
            val halfGaps = gaps / 2

            gameConfig.cellsRange.iterate { position ->
                val id = position.id
                val startCoordinatesPos = cubeCoordinatesCount * id
                val startIndexesPos = cubeIndexesCount * id

                fun fillCoordinates() {
                    val rra = cellSpaceWithGaps.times(position.getVec())
                    val ra = rra - cubesHalfDims

                    centers[id] = ra + cellWithGapsHalfSpace

                    val a = ra + halfGaps
                    val b = a + cellSpace

                    for (i in 0 until cubeCoordinatesCount) {
                        val point = if (coordinatesTemplateArray[i] < 0) a else b
                        trianglesCoordinates[startCoordinatesPos + i] = point[i % 3]
                    }
                }

                fun fillIndexes() {
                    for (i in 0 until cubeIndexesCount) {
                        indexes[startIndexesPos + i] =
                            indexesArray[i] + startCoordinatesPos / 3
                    }
                }

                fillCoordinates()
                fillIndexes()
            }

            return CubeCoordinates(
                trianglesCoordinates,
                indexes,
                centers
            )
        }
    }
}