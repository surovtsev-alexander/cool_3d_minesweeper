package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common

import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.Model_GLSL_Program

abstract class IGLObject(val modelGlslProgram: Model_GLSL_Program) {
    abstract fun bind_attributes()
    abstract fun draw()
}