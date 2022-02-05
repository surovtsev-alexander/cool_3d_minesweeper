package com.surovtsev.gamelogic.minesweeper.camerainfohelperholder

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
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

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
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