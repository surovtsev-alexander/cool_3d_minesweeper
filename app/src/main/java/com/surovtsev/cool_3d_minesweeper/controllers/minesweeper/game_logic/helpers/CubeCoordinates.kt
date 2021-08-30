package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers

import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import glm_.vec3.Vec3

class CubeCoordinates(
    val trianglesCoordinates: FloatArray,
    val indexes: ShortArray,
    val centers: Array<Vec3>
) {
    companion object {
        val coordinatesTemplateArray = intArrayOf(
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
        val extendedIndexesTemplateArray = shortArrayOf(
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
            shortArrayOf(
                extendedIndexesTemplateArray[it * 3 + 2],
                extendedIndexesTemplateArray[it * 3 + 1],
                extendedIndexesTemplateArray[it * 3]
            ).asIterable()
        }.flatten().toShortArray()

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

            val counts = gameConfig.counts
            val dimensions = gameConfig.dimensions
            val gaps = gameConfig.gaps
            val cubesCount = gameConfig.cubesCount
            val cubeCoordinatesCount = coordinatesTemplateArray.size
            val cubeIndexesCount = indexesArray.size
            val coordinatesCount = cubeCoordinatesCount * cubesCount
            val indexesCount = cubeIndexesCount * cubesCount

            val trianglesCoordinates = FloatArray(coordinatesCount)
            val indexes = ShortArray(indexesCount)

            val centers = Array<Vec3>(cubesCount) { Vec3() }

            val cubesHalfDims = dimensions / 2
            val cellSpaceWithGaps = gameConfig.cellSpaceWithGaps
            val cellWithGapsHalfSpace = cellSpaceWithGaps / 2
            val cellSpace = gameConfig.cellSpace
            val halfGaps = gaps / 2

            CubeSkin.iterateCubes(counts) { posision ->
                val id = posision.id
                val startCoordinatesPos = cubeCoordinatesCount * id
                val startIndexesPos = cubeIndexesCount * id

                fun fillCoordinates() {
                    val rra = cellSpaceWithGaps.times(posision.getVec())
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
                            (indexesArray[i] + startCoordinatesPos / 3).toShort()
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