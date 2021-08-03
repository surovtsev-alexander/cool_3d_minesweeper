package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram

class GLCubes(context: Context, val cubes: Cubes) {

    val glObject = ModelObject(context, cubes.triangleCoordinates,
        cubes.trianglesNums, cubes.trianglesTextures,
        cubes.textureCoordinates)
}