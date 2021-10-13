package com.surovtsev.cool3dminesweeper.models.gles.gameviewsholder
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.cool3dminesweeper.views.opengl.CubeOpenGLModel
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
