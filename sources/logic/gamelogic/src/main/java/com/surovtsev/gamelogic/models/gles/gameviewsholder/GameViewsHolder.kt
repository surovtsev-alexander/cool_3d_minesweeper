package com.surovtsev.gamelogic.models.gles.gameviewsholder

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel
import javax.inject.Inject

@GameScope
class GameViewsHolder @Inject constructor(
    private val pointerOpenGLModel: PointerOpenGLModel,
    val cubeOpenGLModel: CubeOpenGLModel,
    private val cubeInfo: CubeInfo
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

        cubeOpenGLModel.updateTexture(cubeInfo.cubeSkin)

        processedActions.processOnSurfaceChanged()
    }

    fun onDrawFrame() {
        pointerOpenGLModel.drawModel()
        cubeOpenGLModel.drawModel()
    }
}
