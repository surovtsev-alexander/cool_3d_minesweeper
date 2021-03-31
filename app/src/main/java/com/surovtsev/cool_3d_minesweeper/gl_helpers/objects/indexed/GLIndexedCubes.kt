package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.indexed

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.IndexedCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program

class GLIndexedCubes(val glslProgram: GLSL_Program,
                     val indexedCubes: IndexedCubes
) {

    val indexed_object = GLIndexedObject(
        glslProgram, indexedCubes.trianglesCoordinates, indexedCubes.indexes)
}
