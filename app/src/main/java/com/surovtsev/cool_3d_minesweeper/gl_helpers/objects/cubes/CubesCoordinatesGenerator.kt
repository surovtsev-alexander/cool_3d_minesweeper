package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CollisionCubes
import glm_.vec3.Vec3
import glm_.vec3.Vec3i

class CubesCoordinatesGenerator(val trianglesCoordinates: FloatArray,
                                val indexes: ShortArray,
                                val collisionCubes: CollisionCubes) {
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

        fun generateCubesCoordinates(
            cubesCoordinatesGeneratorConfig: CubesCoordinatesGeneratorConfig
        ): CubesCoordinatesGenerator {
            val indexesArray = invExtendedIndexedArray

            val counts = cubesCoordinatesGeneratorConfig.counts
            val dimensions = cubesCoordinatesGeneratorConfig.dimensions
            val gaps = cubesCoordinatesGeneratorConfig.gaps
            val cubesCount = counts.x * counts.y * counts.z
            val cubeCoordinatesCount = coordinatesTemplateArray.size
            val cubeIndexesCount = indexesArray.size
            val coordinatesCount = cubeCoordinatesCount * cubesCount
            val indexesCount = cubeIndexesCount * cubesCount

            val trianglesCoordinates = FloatArray(coordinatesCount)
            val indexes = ShortArray(indexesCount)

            val centers = Array<Vec3>(cubesCount) { Vec3() }

            val cubesHalfDims = dimensions / 2
            val cubeSpace = dimensions / counts
            val cubeHalfSpace = cubeSpace / 2
            val cubeDims = cubeSpace - gaps
            val halfGaps = gaps / 2

            val cubeSphereRaius = cubeDims.length() / 2

            for (x in 0 until counts.x) {
                for (y in 0 until counts.y) {
                    for (z in 0 until counts.z) {
                        val id = CollisionCubes.calcId(counts, x, y, z)
                        val startCoordinatesPos = cubeCoordinatesCount * id
                        val startIndexesPos = cubeIndexesCount * id

                        fun fillCoordinates() {
                            val rra = cubeSpace.times(Vec3i(x, y, z))
                            val ra = rra - cubesHalfDims

                            centers[id] = ra + cubeHalfSpace

                            val a = ra + halfGaps
                            val b = a + cubeDims

                            for (i in 0 until cubeCoordinatesCount) {
                                val p = if (coordinatesTemplateArray[i] < 0) a else b
                                trianglesCoordinates[startCoordinatesPos + i] = p[i % 3]
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
                }
            }

            if (false) {
                var test_str = "\ntriangleCoordinates:\n"
                for (i in 0 until trianglesCoordinates.size / 3) {
                    test_str += "${trianglesCoordinates[i * 3]}\t${trianglesCoordinates[i * 3 + 1]}\t${trianglesCoordinates[i * 3 + 2]}\n"
                }
                test_str += "indexes:\n"
                for (i in 0 until indexes.size / 3) {
                    test_str += "${indexes[i * 3]}\t${indexes[i * 3 + 1]}\t${indexes[i * 3 + 2]}\n"
                }
                Log.d("TEST", test_str)
            }

            val collisionCubes = CollisionCubes(counts, cubeSphereRaius, centers)

            return CubesCoordinatesGenerator(trianglesCoordinates, indexes, collisionCubes)
        }
    }
}