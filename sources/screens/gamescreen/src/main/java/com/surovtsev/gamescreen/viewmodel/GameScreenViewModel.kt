package com.surovtsev.gamescreen.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.viewmodel.*
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventToMinesweeper
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.dagger.DaggerGameScreenComponent
import com.surovtsev.gamescreen.dagger.GameScreenComponent
import com.surovtsev.gamescreen.viewmodel.helpers.eventhandlerhelpers.EventCheckerImp
import com.surovtsev.gamescreen.viewmodel.helpers.eventhandlerhelpers.EventProcessorImp
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

const val LoadGameParameterName = "load_game"

typealias GameScreenStateFlow = ScreenStateFlow<GameScreenData>

typealias GameScreenEventHandler = EventHandler<EventToGameScreenViewModel>

typealias GLSurfaceViewCreated = (gLSurfaceView: GLSurfaceView) -> Unit

typealias GameScreenErrorDialogPlacer = ErrorDialogPlacer<EventToGameScreenViewModel, GameScreenData>

class GameScreenViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel<EventToGameScreenViewModel, GameScreenData>(
        EventToGameScreenViewModel.MandatoryEvents,
        GameScreenData.NoData,
        GameScreenInitialState,
    ),
    GameScreenEventHandler,
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<GameScreenViewModel>

    private val gameScreenComponent: GameScreenComponent =
        DaggerGameScreenComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .gameScreenStateFlow(screenStateFlow)
            .build()

    override val eventHandler: com.surovtsev.finitestatemachine.eventhandler.EventHandler<EventToGameScreenViewModel, GameScreenData> =
        com.surovtsev.finitestatemachine.eventhandler.EventHandler(
            EventCheckerImp(),
            EventProcessorImp(
                gameScreenComponent,
                stateHolder,
            ),
        )
    // look ::onDestroy
    @SuppressLint("StaticFieldLeak")
    private var gLSurfaceView: GLSurfaceView? = null

    override fun onCreate(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onCreate(owner)
        gameScreenComponent
            .restartableCoroutineScopeComponent
            .subscriberImp.restart()
    }

    override fun onResume(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onResume(owner)
        gLSurfaceView?.onResume()

        if (stateHolder.state.value.data is GameScreenData.GameMenu) {
            handleEvent(
                EventToGameScreenViewModel.SetIdleState
            )
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onPause(owner)
        gLSurfaceView?.onPause()

        gameScreenComponent.gameComponent.minesweeper.eventHandler.handleEventWithBlocking(
            EventToMinesweeper.SaveGame
        )

        if (stateHolder.state.value.data !is GameScreenData.GameMenu) {
            handleEvent(
                EventToGameScreenViewModel.OpenGameMenuAndSetLoadingState
            )
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onDestroy(owner)

        gLSurfaceView = null
    }

    override suspend fun processEvent(event: EventToGameScreenViewModel): EventProcessingResult<EventToGameScreenViewModel> {
        return eventHandler.eventProcessor.processEvent(event)
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

//    override fun onKeyDown(keyCode: Int): Boolean {
//        if (
//            keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
//            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
//        ) {
//            flaggingEvent.onDataChanged(
//                !(flaggingEvent.valueOrDefault)
//            )
//
//            return true
//        }
//
//        return false
//    }


}

