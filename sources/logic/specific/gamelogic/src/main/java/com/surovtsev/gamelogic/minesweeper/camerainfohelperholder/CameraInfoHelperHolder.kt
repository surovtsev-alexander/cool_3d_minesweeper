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


package com.surovtsev.gamelogic.minesweeper.camerainfohelperholder

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.gles.renderer.ScreenResolution
import com.surovtsev.utils.gles.renderer.ScreenResolutionFlow
import com.surovtsev.utils.math.camerainfo.CameraInfo
import com.surovtsev.utils.math.camerainfo.CameraInfoHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@GameScope
class CameraInfoHelperHolder @Inject constructor(
    private val gameStateHolder: GameStateHolder,
    private val screenResolutionFlow: ScreenResolutionFlow,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {

    private val _cameraInfoHelperFlow = MutableStateFlow<CameraInfoHelper?>(null)
    val cameraInfoHelperFlow = _cameraInfoHelperFlow.asStateFlow()

    init {
        subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        restartableCoroutineScope.launch {
            gameStateHolder.gameStateFlow.combine(
                screenResolutionFlow
            ) { gameState, screenResolution ->
                gameState?.cameraInfo to screenResolution
            }.collectLatest { (cameraInfo, screenResolution) ->
                _cameraInfoHelperFlow.value = if (cameraInfo == null) {
                    null
                } else {
                    createCameraInfoHelper(
                        cameraInfo, screenResolution
                    )
                }
            }
        }
    }

    private fun createCameraInfoHelper(
        cameraInfo: CameraInfo,
        screenResolution: ScreenResolution,
    ): CameraInfoHelper {
        return CameraInfoHelper(
            cameraInfo,
            screenResolution
        )
    }
}