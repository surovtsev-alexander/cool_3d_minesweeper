package com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.*
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponentEntryPoint
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.helpers.GameViewEvents
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.helpers.MarkingEvent
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.presentation.game_screen.LoadGameParameterName
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class GameActivityViewModel @Inject constructor(
    gameComponentProvider: Provider<GameComponent.Builder>,
    savedStateHandle: SavedStateHandle
):
    ViewModel(),
    IHandlePauseResumeDestroyKeyDown,
    LifecycleObserver
{
    private val markingEvent: MarkingEvent
    val minesweeperController: MinesweeperController
    private val gameRenderer: GLESRenderer
    val gLSurfaceView: GLSurfaceView
    val gameViewEvents: GameViewEvents
    val gameControls: GameControls

    init {
        val loadGame = savedStateHandle.get<String>(LoadGameParameterName).toBoolean()

        val gameComponent = gameComponentProvider
            .get()
            .loadGame(loadGame)
            .build()
        val gameComponentEntryPoint = EntryPoints.get(
            gameComponent, GameComponentEntryPoint::class.java
        )

        markingEvent =
            gameComponentEntryPoint.markingEvent
        minesweeperController =
            gameComponentEntryPoint.minesweeperController
        gameRenderer =
            gameComponentEntryPoint.gameRenderer
        gLSurfaceView =
            gameComponentEntryPoint.gLSurfaceView
        gameViewEvents =
            gameComponentEntryPoint.gameViewEvents
        gameControls =
            gameComponentEntryPoint.gameControls

        Log.d("TEST+++", "GameActivityViewModel loadGame ${gameComponentEntryPoint.loadGame}")

    }

    @Suppress("Unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        gLSurfaceView.apply {
            minesweeperController.touchListener.connectToGLSurfaceView(
                gLSurfaceView
            )

            setEGLContextClientVersion(2)
            setRenderer(gameRenderer)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
        gLSurfaceView.onPause()
        minesweeperController.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        gLSurfaceView.onResume()
        minesweeperController.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
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