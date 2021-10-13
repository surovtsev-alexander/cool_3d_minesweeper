package com.surovtsev.cool3dminesweeper.models.gles.gameviewsholder
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.gles.view.pointer.GLPointerModel
import com.surovtsev.cool3dminesweeper.views.opengl.CubeView
import javax.inject.Inject

@GameScope
class GameViewsHolder @Inject constructor(
    private val glPointerModel: GLPointerModel,
    val cubeView: CubeView
) {
    fun onSurfaceCreated() {
        glPointerModel.onSurfaceCreated()
        cubeView.onSurfaceCreated()
    }
}
