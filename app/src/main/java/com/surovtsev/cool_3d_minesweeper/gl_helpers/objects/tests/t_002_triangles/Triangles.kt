package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.tests.t_002_triangles

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.GLObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program

class Triangles(val glslProgram: GLSL_Program) {

    private val triangleCoordinates = floatArrayOf(
        1f, 1f, 1f,
        1f, 2f, 1f,
        2f, 1f, 1f,
        0f, -1f, 2f,
        0f, -0f, 2f,
        -1f,-0f, 2f,
    )
    private val triangleNumbers = floatArrayOf(
        0f, 0f, 0f,
        1f, 1f, 1f,
    )
    private val triangleTextures = floatArrayOf(
        0f, 0f, 0f,
        1f, 1f, 1f,
    )

    val glslObject = GLObject(glslProgram, triangleCoordinates, triangleNumbers, triangleTextures)
}