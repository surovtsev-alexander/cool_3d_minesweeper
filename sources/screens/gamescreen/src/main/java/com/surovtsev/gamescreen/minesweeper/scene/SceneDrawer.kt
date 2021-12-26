package com.surovtsev.gamescreen.minesweeper.scene

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.CameraInfoHelper
import com.surovtsev.gamescreen.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamescreen.views.opengl.CubeOpenGLModel
import glm_.vec2.Vec2i
import javax.inject.Inject
import javax.inject.Named

@GameScope
class SceneDrawer @Inject constructor(
    private val cameraInfoHelper: CameraInfoHelper,
    private val pointerOpenGLModel: PointerOpenGLModel,
    private val cubeOpenGLModel: CubeOpenGLModel,
    @Named(PointerEnabledName)
    private val pointerEnabled: Boolean,
) {

    companion object {
        const val PointerEnabledName = "pointerEnabled"
    }

    fun onSurfaceChanged(newDisplaySize: Vec2i) {
        cameraInfoHelper.onSurfaceChanged(newDisplaySize)
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
