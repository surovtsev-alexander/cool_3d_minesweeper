package com.surovtsev.gamelogic.minesweeper.gameState

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.gles.renderer.ScreenResolutionFlow
import com.surovtsev.utils.math.camerainfo.CameraInfo
import com.surovtsev.utils.math.camerainfo.CameraInfoHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@GameScope
class CameraInfoHelperHolder @Inject constructor(
    private val gameStateHolder: GameStateHolder,
    private val screenResolutionFlow: ScreenResolutionFlow,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {

    val cameraInfoHelper: CameraInfoHelper = CameraInfoHelper(
        CameraInfo()
    )

//    private val _cameraInfoHelperFlow = MutableStateFlow(
//        createCameraInfoHelper(
//            CameraInfo(),
//            DefaultScreenResolution
//        ))
//    val cameraInfoHelperFlow = _cameraInfoHelperFlow.asStateFlow()

    init {
        subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            screenResolutionFlow.collectLatest {
                cameraInfoHelper.onSurfaceChanged(it)
            }
        }

//        customCoroutineScope.launch {
//
//            gameStateHolder.gameStateFlow.zip(
//                screenResolutionFlow
//            ) { gameState, screenResolution ->
//                gameState.cameraInfo to screenResolution
//            }.stateIn(customCoroutineScope).collectLatest { (cameraInfo, screenResolution) ->
//                _cameraInfoHelperFlow.value = createCameraInfoHelper(
//                    cameraInfo, screenResolution
//                )
//            }
//        }
    }

//    private fun createCameraInfoHelper(
//        cameraInfo: CameraInfo,
//        screenResolution: ScreenResolution,
//    ): CameraInfoHelper {
//        return CameraInfoHelper(
//            cameraInfo,
//            screenResolution
//        )
//    }
}