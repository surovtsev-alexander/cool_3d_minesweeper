package com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model

import android.opengl.GLSurfaceView
import android.util.Log
import android.view.KeyEvent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameScope
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.helpers.MarkingEvent
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import javax.inject.Inject

@GameScope
class GameActivityViewModel @Inject constructor(
    var markingEvent: MarkingEvent,
    var minesweeperController: MinesweeperController,
    var gameRenderer: GLESRenderer,
    var gLSurfaceView: GLSurfaceView
):
    IHandlePauseResumeDestroyKeyDown
{
    var glSurfaceViewPrepared: Boolean = false

    fun prepareGlSurfaceView() {
        if (glSurfaceViewPrepared) {
            return
        }
        glSurfaceViewPrepared = true

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
                !(markingEvent.valueOrDefault)
            )

            return true
        }

        return false
    }
}