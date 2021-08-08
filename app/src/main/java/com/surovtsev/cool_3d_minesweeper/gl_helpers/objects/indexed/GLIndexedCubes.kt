package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.indexed

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.RawCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram

class GLIndexedCubes(val modelGlslProgram: ModelGLSLProgram,
                     val indexedCubes: RawCubes
) {

    val indexed_object = GLIndexedObject(
        modelGlslProgram, indexedCubes.trianglesCoordinates, indexedCubes.indexes)
}
