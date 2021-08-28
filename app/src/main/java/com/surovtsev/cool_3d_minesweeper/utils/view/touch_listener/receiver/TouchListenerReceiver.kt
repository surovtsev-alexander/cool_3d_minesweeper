package com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.receiver

import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces.*

data class TouchListenerReceiver(
    val clickEventQueueHandler: GLSurfaceView,
    val touchReceiver: ITouchReceiverCalculator,
    val rotationReceiver: IRotationReceiverCalculator,
    val scaleReceiver: IScaleReceiverCalculator,
    val moveReceiver: IMoveReceiverCalculator
)
