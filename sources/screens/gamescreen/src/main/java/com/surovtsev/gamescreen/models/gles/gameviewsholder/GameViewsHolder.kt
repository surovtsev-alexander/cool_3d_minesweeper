package com.surovtsev.gamescreen.models.gles.gameviewsholder

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamescreen.views.opengl.CubeOpenGLModel
import javax.inject.Inject

@GameScope
class GameViewsHolder @Inject constructor(
    private val pointerOpenGLModel: PointerOpenGLModel,
    val cubeOpenGLModel: CubeOpenGLModel
) {
    fun onSurfaceCreated() {
        pointerOpenGLModel.onSurfaceCreated()
        cubeOpenGLModel.onSurfaceCreated()
    }
}
