package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CollisionCubes
import com.surovtsev.cool_3d_minesweeper.math.Point3d
import glm_.vec3.Vec3i
import glm_.vec3.Vec3s

class RawCubes(val trianglesCoordinates: FloatArray,
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
            1, 2, 0,
            0, 2, 3,
            0, 3, 4,
            4, 3, 7,

            3, 2, 7,
            7, 2, 6,
            2, 1, 6,
            6, 1, 5,

            1, 0, 5,
            5, 0, 4,
            5, 4, 6,
            6, 4, 7,
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

        fun rawCubes(
            cubesConfig: CubesConfig
        ): RawCubes {
            val indexesArray = invExtendedIndexedArray

            val counts = cubesConfig.counts
            val dimensions = cubesConfig.dimensions
            val gaps = cubesConfig.gaps
            val cubesCount = counts.x * counts.y * counts.z
            val cubeCoordinatesCount = coordinatesTemplateArray.size
            val cubeIndexesCount = indexesArray.size
            val coordinatesCount = cubeCoordinatesCount * cubesCount
            val indexesCount = cubeIndexesCount * cubesCount

            val trianglesCoordinates = FloatArray(coordinatesCount)
            val indexes = ShortArray(indexesCount)

            val centers = Array<Point3d<Float>>(cubesCount) { _ -> Point3d<Float>(0f, 0f, 0f) }

            val cubesHalfDims = Point3d.divide(dimensions, 2)
            val cubeSpace = Point3d.divideShort(dimensions, counts)
            val cubeHalfSpace = Point3d.divide(cubeSpace, 2)
            val cubeDims = Point3d.minus(cubeSpace, gaps)
            val halfGaps = Point3d.divide(gaps, 2)

            val cubeSphereRaius = Point3d.len(cubeDims) / 2

            val xx = { i: Int ->
                when (i % 3) {
                    0 -> { p: Point3d<Float> -> p.x }
                    1 -> { p: Point3d<Float> -> p.y }
                    else -> { p: Point3d<Float> -> p.z }
                }
            }

            for (x in 0 until counts.x) {
                for (y in 0 until counts.y) {
                    for (z in 0 until counts.z) {
                        val id = CollisionCubes.calcId(counts, x, y, z)
                        val startCoordinatesPos = cubeCoordinatesCount * id
                        val startIndexesPos = cubeIndexesCount * id

                        fun fillCoordinates() {
                            val rra = Point3d.multiply(cubeSpace, Point3d<Int>(x, y, z))
                            val ra = Point3d.minus(rra, cubesHalfDims)

                            centers[id] = Point3d.plus(ra, cubeHalfSpace)

                            val a = Point3d.plus(ra, halfGaps)
                            val b = Point3d.plus(a, cubeDims)

                            for (i in 0 until cubeCoordinatesCount) {
                                val p = if (coordinatesTemplateArray[i] < 0) a else b
                                trianglesCoordinates[startCoordinatesPos + i] = xx(i)(p)
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

            return RawCubes(trianglesCoordinates, indexes, collisionCubes)
        }
    }
}