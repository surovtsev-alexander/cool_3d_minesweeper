package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.indexed

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.IndexedCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.Model_GLSL_Program

class GLIndexedCubes(val modelGlslProgram: Model_GLSL_Program,
                     val indexedCubes: IndexedCubes
) {

    val indexed_object = GLIndexedObject(
        modelGlslProgram, indexedCubes.trianglesCoordinates, indexedCubes.indexes)
}
