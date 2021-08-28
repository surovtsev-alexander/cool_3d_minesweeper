package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.tests.t_001_trianle

import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.indexed.GLIndexedObject
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs.ModelGLSLProgram

class Triangle(val modelGlslProgram: ModelGLSLProgram) {

    private val trianglesCoordinates = floatArrayOf(
        -1f, -1f, 0f,
        12f, 100f, 123f,
        1f, -1f, 0f,
        0f, 1f, 0f
    )

    private val indexes = shortArrayOf(
        0, 2, 3
    )

    val indexedObject = GLIndexedObject(modelGlslProgram, trianglesCoordinates, indexes)
}