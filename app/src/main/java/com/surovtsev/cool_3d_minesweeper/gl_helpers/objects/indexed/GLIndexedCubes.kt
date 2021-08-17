package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.indexed

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram

class GLIndexedCubes(val modelGlslProgram: ModelGLSLProgram,
                     val indexedCubesCoordinatesGenerator: CubesCoordinatesGenerator
) {

    val indexedObject = GLIndexedObject(
        modelGlslProgram, indexedCubesCoordinatesGenerator.trianglesCoordinates, indexedCubesCoordinatesGenerator.indexes)
}
