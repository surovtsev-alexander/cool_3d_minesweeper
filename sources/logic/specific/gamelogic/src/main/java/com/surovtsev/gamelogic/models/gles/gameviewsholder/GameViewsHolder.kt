package com.surovtsev.gamelogic.models.gles.gameviewsholder

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@GameScope
class GameViewsHolder @Inject constructor(
    private val pointerOpenGLModel: PointerOpenGLModel,
    private val cubeOpenGLModel: CubeOpenGLModel,
    private val gameStateHolder: GameStateHolder,
    private val cameraInfoHelperHolder: CameraInfoHelperHolder,

    subscriptionsHolder: SubscriptionsHolder,
): Subscription {
    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            gameStateHolder.gameStateFlow.collectLatest {
                withContext(Dispatchers.Main) {
                    processedActions.flush()
                }
            }
        }
    }

    private class ProcessedActions {
        var onSurfaceCreated = false
            private set
        var onSurfaceChanged = false
            private set

        fun flush() {
            onSurfaceChanged = false
            onSurfaceCreated = false
        }

        fun processOnSurfaceCreated() {
            onSurfaceCreated = true
            onSurfaceChanged = false
        }

        fun processOnSurfaceChanged() {
            onSurfaceChanged = true
        }
    }

    private val processedActions = ProcessedActions()

    init {
        subscriptionsHolder.addSubscription(
            this
        )
    }

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

        cameraInfoHelperHolder.cameraInfoHelperFlow.value?.update()
        gameStateHolder.gameStateFlow.value?.let {
            cubeOpenGLModel.updateTexture(it.cubeInfo.cubeSkin)
        }

        processedActions.processOnSurfaceChanged()
    }

    fun onDrawFrame() {
        pointerOpenGLModel.drawModel()
        cubeOpenGLModel.drawModel()
    }
}
