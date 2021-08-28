package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.indexed

import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs.ModelGLSLProgram

class GLIndexedCubes(val modelGlslProgram: ModelGLSLProgram,
                     val indexedCubesCoordinatesGenerator: CubesCoordinatesGenerator
) {

    val indexedObject = GLIndexedObject(
        modelGlslProgram, indexedCubesCoordinatesGenerator.trianglesCoordinates, indexedCubesCoordinatesGenerator.indexes)
}
