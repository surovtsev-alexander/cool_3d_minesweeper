package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common

import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram

abstract class IGLObject(val modelGlslProgram: ModelGLSLProgram) {
    abstract fun bind_attributes()
    abstract fun draw()
}