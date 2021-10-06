package com.surovtsev.cool3dminesweeper.models.gles.game_views_holder
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool3dminesweeper.views.opengl.CubeView
import javax.inject.Inject

@GameScope
class GameViewsHolder @Inject constructor(
    private val glPointerView: GLPointerView,
    val cubeView: CubeView
) {
    fun onSurfaceCreated() {
        glPointerView.onSurfaceCreated()
        cubeView.onSurfaceCreated()
    }
}
