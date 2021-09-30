package com.surovtsev.cool_3d_minesweeper.models.gles.game_views_holder
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameScope
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.views.opengl.CubeView
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
