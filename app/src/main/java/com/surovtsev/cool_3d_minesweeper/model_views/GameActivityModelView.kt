package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.MarkingEvent
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import javax.inject.Inject

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

    @Inject
    lateinit var gLSurfaceView: GLSurfaceView

    init {
        context.daggerComponentsHolder.createAndGetGameControllerComponent()
            .inject(this)
    }

    fun prepareGlSurfaceView() {
        gLSurfaceView.apply {
            minesweeperController.touchListener.connectToGLSurfaceView(
                gLSurfaceView
            )
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