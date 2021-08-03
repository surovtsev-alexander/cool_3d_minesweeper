package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.Model_GLSL_Program

class GLCubes(val modelGlslProgram: Model_GLSL_Program,
              val cubes: Cubes,
              val textureId: Int
) {

    val glObject = ModelObject(modelGlslProgram, cubes.triangleCoordinates,
        cubes.trianglesNums, cubes.trianglesTextures,
        cubes.textureCoordinates,
        textureId
    )
}