package com.surovtsev.gamelogic.models.gles.gameviewsholder

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.gameState.GameState
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import javax.inject.Inject

@GameScope
class GameViewsHolder @Inject constructor(
    private val pointerOpenGLModel: PointerOpenGLModel,
    private val cubeOpenGLModel: CubeOpenGLModel,
    private val gameState: GameState,
) {
    private class ProcessedActions {
        var onSurfaceCreated = false
            private set
        var onSurfaceChanged = false
            private set

        fun processOnSurfaceCreated() {
            onSurfaceCreated = true
            onSurfaceChanged = false
        }

        fun processOnSurfaceChanged() {
            onSurfaceChanged = true
        }
    }

    private val processedActions = ProcessedActions()

    fun onSurfaceCreated(
        force: Boolean = true
    ) {
        if (processedActions.onSurfaceCreated && !force) {
            return
        }

        pointerOpenGLModel.onSurfaceCreated()
        cubeOpenGLModel.onSurfaceCreated()

        processedActions.processOnSurfaceCreated()
    }

    fun onSurfaceChanged(
        force: Boolean = true
    ) {
        if (processedActions.onSurfaceChanged && !force) {
            return
        }

        onSurfaceCreated(force = false)

        cubeOpenGLModel.updateTexture(gameState.cubeInfo.cubeSkin)

        processedActions.processOnSurfaceChanged()
    }

    fun onDrawFrame() {
        pointerOpenGLModel.drawModel()
        cubeOpenGLModel.drawModel()
    }
}
