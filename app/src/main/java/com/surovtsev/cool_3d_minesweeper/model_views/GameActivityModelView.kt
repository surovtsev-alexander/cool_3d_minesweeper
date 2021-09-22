package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameViewEventsNames
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.MarkingEvent
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControlsNames
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.MarkOnShortTapControl
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import javax.inject.Inject
import javax.inject.Named

class GameActivityModelView(
    context: Context
):
    IHandlePauseResumeDestroyKeyDown
{
    @Inject
    lateinit var markingEvent: MarkingEvent

    @Inject
    lateinit var gameEventsReceiver: GameEventsReceiver

    @Inject
    lateinit var minesweeperController: MinesweeperController
    @Inject
    lateinit var gameRenderer: GLESRenderer

    init {
        context.daggerComponentsHolder.createAndGetGameControllerComponent()
            .inject(this)
    }

    private fun assignTouchListenerToGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        val touchReceiverCalculator = object: ITouchReceiverCalculator {
            override fun getReceiver(): ITouchReceiver = minesweeperController.touchReceiver
        }
        val rotationReceiverCalculator = object: IRotationReceiverCalculator {
            override fun getReceiver(): IRotationReceiver = minesweeperController.scene.moveHandler
        }
        val scaleReceiverCalculator = object: IScaleReceiverCalculator {
            override fun getReceiver(): IScaleReceiver = minesweeperController.scene.moveHandler
        }
        val moveReceiverCalculator = object: IMoveReceiverCalculator {
            override fun getReceiver(): IMoveReceiver = minesweeperController.scene.moveHandler
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

    fun prepareGlSurfaceView(gLSurfaceView: GLSurfaceView) {
        gLSurfaceView.apply {
            assignTouchListenerToGLSurfaceView(this)
            setEGLContextClientVersion(2)
            setRenderer(gameRenderer)
        }
    }

    override fun onPause() {
        minesweeperController.onPause()
    }

    override fun onResume() {
        minesweeperController.onResume()
    }

    override fun onDestroy() {
        minesweeperController.onDestroy()
    }

    override fun onKeyDown(keyCode: Int): Boolean {
        if (
            keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        ) {
            markingEvent.onDataChanged(
                !(markingEvent.getValueOrDefault())
            )

            return true
        }

        return false
    }
}