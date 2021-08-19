package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.tests.t_002_triangles

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram

class Triangles(context: Context) {

    private val triangleCoordinates = floatArrayOf(
        1f, 1f, 1f,
        1f, 2f, 1f,
        2f, 1f, 1f,
        0f, -1f, 2f,
        0f, -0f, 2f,
        -1f,-0f, 2f
    )
    private val triangleNumbers = floatArrayOf(
        0f, 0f, 0f,
        1f, 1f, 1f
    )
    private val triangleTextures = floatArrayOf(
        0f, 0f, 0f,
        1f, 1f, 1f
    )

    private val isEmpty = floatArrayOf(
        -1f, -1f, -1f,
        -1f, -1f, -1f
    )

    private val textureCoordinates = floatArrayOf(
        0f, 0f,
        1f, 1f,
        0f, 1f,

        1f, 0f,
        1f, 1f,
        0f, 0f
    )

    val glslObject = ModelObject(context, triangleCoordinates,
        isEmpty, textureCoordinates)
}