package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram

class GLCubes(val modelGlslProgram: ModelGLSLProgram,
              val cubes: Cubes,
              val textureId: Int
) {

    val glObject = ModelObject(modelGlslProgram, cubes.triangleCoordinates,
        cubes.trianglesNums, cubes.trianglesTextures,
        cubes.textureCoordinates,
        textureId
    )
}