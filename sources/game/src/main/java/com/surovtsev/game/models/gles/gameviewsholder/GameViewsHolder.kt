package com.surovtsev.game.models.gles.gameviewsholder

import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.game.views.opengl.CubeOpenGLModel
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
