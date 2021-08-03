package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.tests.t_001_trianle

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.indexed.GLIndexedObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.Model_GLSL_Program

class Triangle(val modelGlslProgram: Model_GLSL_Program) {

    private val _trianglesCoordinates = floatArrayOf(
        -1f, -1f, 0f,
        12f, 100f, 123f,
        1f, -1f, 0f,
        0f, 1f, 0f
    )

    private val _indexes = shortArrayOf(
        0, 2, 3
    )

    val indexed_object = GLIndexedObject(modelGlslProgram, _trianglesCoordinates, _indexes)
}