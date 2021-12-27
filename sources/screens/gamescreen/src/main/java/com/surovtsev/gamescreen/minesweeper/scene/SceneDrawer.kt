package com.surovtsev.gamescreen.minesweeper.scene

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamescreen.views.opengl.CubeOpenGLModel
import javax.inject.Inject
import javax.inject.Named

@GameScope
class SceneDrawer @Inject constructor(
    private val pointerOpenGLModel: PointerOpenGLModel,
    private val cubeOpenGLModel: CubeOpenGLModel,
    @Named(PointerEnabledName)
    private val pointerEnabled: Boolean,
) {

    companion object {
        const val PointerEnabledName = "pointerEnabled"
    }

    fun onDrawFrame() {
        if (pointerEnabled) {
            pointerOpenGLModel.mGLESProgram.useProgram()
            pointerOpenGLModel.bindData()
            pointerOpenGLModel.draw()
        }

        cubeOpenGLModel.cubeGLESProgram.useProgram()
        cubeOpenGLModel.bindData()
        cubeOpenGLModel.draw()
    }
}
