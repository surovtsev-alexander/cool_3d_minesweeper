package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program
import com.surovtsev.cool_3d_minesweeper.math.Point3d

class Cubes(val glslProgram: GLSL_Program
    , val triangleCoordinates: FloatArray
    , val indexes: ShortArray) {

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

        fun simpleCube(glslProgram: GLSL_Program): Cubes {
            val trianglesCoordinates = coordinatesTemplateArray.map { it.toFloat() }.toFloatArray()
            val indexes = extendedIndexesTemplateArray.clone()

            return Cubes(glslProgram, trianglesCoordinates, indexes)
        }


        fun cubes(glslProgram: GLSL_Program
                  , counts: Point3d<Short>
                  , dimensions: Point3d<Float>
                  , gaps: Point3d<Float>): Cubes {
            val cubesCount = counts.x * counts.y * counts.z
            val cubeCoordinatesCount = coordinatesTemplateArray.size
            val cubeIndexesCount = extendedIndexesTemplateArray.size
            val coordinatesCount = cubeCoordinatesCount * cubesCount
            val indexesCount = cubeIndexesCount * cubesCount

            val trianglesCoordinates = FloatArray(coordinatesCount)
            val indexes = ShortArray(indexesCount)

            val cubesHalfDims = Point3d.divide(dimensions, 2)
            val cubeSpace = Point3d.divideShort(dimensions, counts)
            val cubeDims = Point3d.minus(cubeSpace, gaps)
            val halfGaps = Point3d.divide(gaps, 2)

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
                        val id = x + counts.x * (y + counts.y * z)
                        val startCoordinatesPos = cubeCoordinatesCount * id
                        val startIndexesPos = cubeIndexesCount * id

                        fun fillCoordinates() {
                            val rra = Point3d.multiply(cubeSpace, Point3d<Int>(x, y, z))
                            val ra = Point3d.minus(rra, cubesHalfDims)

                            val a = Point3d.plus(ra, halfGaps)
                            val b = Point3d.plus(ra, cubeDims)

                            for (i in 0 until cubeCoordinatesCount) {
                                val p = if (coordinatesTemplateArray[i] < 0) a else b
                                trianglesCoordinates[startCoordinatesPos + i] = xx(i)(p)
                            }
                        }

                        fun fillIndexes() {
                            for (i in 0 until cubeIndexesCount) {
                                indexes[startIndexesPos + i] =
                                    (extendedIndexesTemplateArray[i] + startCoordinatesPos / 3).toShort()
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

            return Cubes(glslProgram, trianglesCoordinates, indexes)
        }
    }

    val indexed_object = IndexedObject(glslProgram
        , triangleCoordinates, indexes)
}
