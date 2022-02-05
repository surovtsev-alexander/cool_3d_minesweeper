package com.surovtsev.gamelogic.utils.utils.gles

import com.surovtsev.gamelogic.utils.gles.model.program.GLESProgram

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
