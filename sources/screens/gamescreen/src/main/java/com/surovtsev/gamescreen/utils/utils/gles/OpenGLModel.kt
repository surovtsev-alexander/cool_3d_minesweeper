package com.surovtsev.gamescreen.utils.utils.gles

import com.surovtsev.gamescreen.utils.gles.model.program.GLESProgram

abstract class OpenGLModel(
    val gLESProgram: GLESProgram
) {
    abstract fun bindData()
    abstract fun draw()

    open fun drawModel() {
        gLESProgram.useProgram()
        bindData()
        draw()
    }
}
