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


package com.surovtsev.gamescreen.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.templateviewmodel.TemplateViewModel
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventToMinesweeper
import com.surovtsev.gamescreen.dagger.DaggerGameScreenComponent
import com.surovtsev.gamescreen.dagger.GameScreenComponent
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

const val LoadGameParameterName = "load_game"

class GameScreenViewModel @AssistedInject constructor(
    @Suppress("UNUSED_PARAMETER") @Assisted savedStateHandle: SavedStateHandle,
    @Suppress("UNUSED_PARAMETER") @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateViewModel(),
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<GameScreenViewModel>

    private val gameScreenComponent: GameScreenComponent =
        DaggerGameScreenComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .screenStateFlow(screenStateFlow)
            .stateHolder(stateHolder)
            .finiteStateMachineFactory(::createFiniteStateMachine)
            .build()

    override val finiteStateMachine =
        gameScreenComponent.finiteStateMachine

    /**
     * Released in ::onDestroy and it is the reason to suppress lint warning.
     *
     * This variable is needed to call onResume and onPause methods of GLSurfaceView.
     */
    @SuppressLint("StaticFieldLeak")
    private var gLSurfaceView: GLSurfaceView? = null

    override fun onCreate(owner: LifecycleOwner) {
        super<TemplateViewModel>.onCreate(owner)
        gameScreenComponent
            .restartableCoroutineScopeComponent
            .subscriberImp.restart()
    }

    override fun onResume(owner: LifecycleOwner) {
        super<TemplateViewModel>.onResume(owner)
        gLSurfaceView?.onResume()

        if (screenStateFlow.value.data is GameScreenData.GameMenu) {
            finiteStateMachine.eventReceiver.receiveEvent(
                EventToGameScreenViewModel.SetIdleState
            )
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super<TemplateViewModel>.onPause(owner)
        gLSurfaceView?.onPause()

        gameScreenComponent.gameComponent.minesweeper.eventHandler.handleEventWithBlocking(
            EventToMinesweeper.SaveGame
        )

        if (screenStateFlow.value.data !is GameScreenData.GameMenu) {
            finiteStateMachine.eventReceiver.receiveEvent(
                EventToGameScreenViewModel.OpenGameMenuAndSetLoadingState
            )
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super<TemplateViewModel>.onDestroy(owner)

        gLSurfaceView = null
    }

    fun initGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        val gameRenderer = gameScreenComponent
            .gLESRenderer
        val touchListener = gameScreenComponent
            .touchListenerComponent
            .touchListener

        gLSurfaceView.setEGLContextClientVersion(2)
        gLSurfaceView.setRenderer(gameRenderer)

        touchListener.connectToGLSurfaceView(
            gLSurfaceView
        )

        this.gLSurfaceView = gLSurfaceView
    }
}

