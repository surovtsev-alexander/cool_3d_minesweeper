/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.models.gles.gameviewsholder

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolder
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
    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        restartableCoroutineScope.launch {
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
            cubeOpenGLModel.updateTexture(it)
        }

        processedActions.processOnSurfaceChanged()
    }

    fun onDrawFrame() {
        pointerOpenGLModel.drawModel()
        cubeOpenGLModel.drawModel()
    }
}
