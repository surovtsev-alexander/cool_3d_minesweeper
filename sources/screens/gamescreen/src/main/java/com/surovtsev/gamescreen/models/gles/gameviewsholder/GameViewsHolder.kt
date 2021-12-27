package com.surovtsev.gamescreen.models.gles.gameviewsholder

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamescreen.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamescreen.views.opengl.CubeOpenGLModel
import logcat.logcat
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
}
