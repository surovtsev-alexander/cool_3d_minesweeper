package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers

import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver
import javax.inject.Inject

@GameControllerScope
class MinesweeperTouchListenerHelper @Inject constructor(
    private val touchReceiver: TouchReceiver,
    private val moveHandler: MoveHandler
) {

    fun assingListenerToGLSurfaceView(gLSurfaceView: GLSurfaceView) {
        val touchReceiverCalculator = object : ITouchReceiverCalculator {
            override fun getReceiver(): ITouchReceiver = touchReceiver
        }
        val rotationReceiverCalculator = object : IRotationReceiverCalculator {
            override fun getReceiver(): IRotationReceiver = moveHandler
        }
        val scaleReceiverCalculator = object : IScaleReceiverCalculator {
            override fun getReceiver(): IScaleReceiver = moveHandler
        }
        val moveReceiverCalculator = object : IMoveReceiverCalculator {
            override fun getReceiver(): IMoveReceiver = moveHandler
        }
        val touchListenerReceiver = TouchListenerReceiver(
            gLSurfaceView,
            touchReceiverCalculator,
            rotationReceiverCalculator,
            scaleReceiverCalculator,
            moveReceiverCalculator,
        )
        val touchListener = TouchListener(touchListenerReceiver)
        gLSurfaceView.setOnTouchListener(touchListener)
    }
}