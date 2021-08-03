package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.indexed

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.IndexedCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram

class GLIndexedCubes(val modelGlslProgram: ModelGLSLProgram,
                     val indexedCubes: IndexedCubes
) {

    val indexed_object = GLIndexedObject(
        modelGlslProgram, indexedCubes.trianglesCoordinates, indexedCubes.indexes)
}
