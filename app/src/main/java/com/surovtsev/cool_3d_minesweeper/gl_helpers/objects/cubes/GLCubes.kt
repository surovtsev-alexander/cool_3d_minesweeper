package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.GLObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program

class GLCubes(val glslProgram: GLSL_Program,
              val cubes: Cubes,
              val textureId: Int
) {

    val glObject = GLObject(glslProgram, cubes.triangleCoordinates,
        cubes.trianglesNums, cubes.trianglesTextures,
        cubes.textureCoordinates,
        textureId
    )
}