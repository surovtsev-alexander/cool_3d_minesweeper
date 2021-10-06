package com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.*
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool3dminesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool3dminesweeper.dagger.app.game.GameComponentEntryPoint
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.GameScreenEvents
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.MarkingEvent
import com.surovtsev.cool3dminesweeper.models.game.interaction.GameControls
import com.surovtsev.cool3dminesweeper.presentation.gamescreen.LoadGameParameterName
import com.surovtsev.cool3dminesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import com.surovtsev.cool3dminesweeper.views.glesrenderer.GLESRenderer
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class GameScreenViewModel @Inject constructor(
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
    val gameScreenEvents: GameScreenEvents
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
        gameScreenEvents =
            gameComponentEntryPoint.gameScreenEvents
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