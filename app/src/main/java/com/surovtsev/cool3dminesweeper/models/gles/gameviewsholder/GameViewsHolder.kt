package com.surovtsev.cool3dminesweeper.models.gles.gameviewsholder

import com.surovtsev.cool3dminesweeper.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.cool3dminesweeper.views.opengl.CubeOpenGLModel
import com.surovtsev.game.dagger.GameScope
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
