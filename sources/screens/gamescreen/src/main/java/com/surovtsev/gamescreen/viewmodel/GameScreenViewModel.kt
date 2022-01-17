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

    // look ::onDestroy
    @SuppressLint("StaticFieldLeak")
    private var gLSurfaceView: GLSurfaceView? = null

    private var gameControlsImp: GameControlsImp? = null
    private var uiGameControlsMutableFlows: UIGameControlsMutableFlows? = null
    private var uiGameControlsFlows: UIGameControlsFlows? = null

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

    override suspend fun handleScreenLeaving(
        owner: LifecycleOwner
    ): EventProcessingResult<EventToGameScreenViewModel> {
        gameScreenComponent
            .restartableCoroutineScopeComponent
            .subscriberImp
            .onStop()

        gameScreenComponent
            .gLESRenderer
            .openGLEventsHandler = null

        gameScreenComponent.gameComponent.minesweeper.eventHandler.handleEventWithBlocking(
            EventToMinesweeper.SetGameStateToNull
        )

        stateHolder.publishIdleState(
            GameScreenData.NoData
        )

        return EventProcessingResult.Processed()
    }

    override suspend fun processEvent(event: EventToGameScreenViewModel): EventProcessingResult<EventToGameScreenViewModel> {
        val eventProcessor = when (event) {
            is EventToGameScreenViewModel.HandleScreenLeaving            -> suspend { handleScreenLeaving(event.owner) }
            is EventToGameScreenViewModel.NewGame                        -> suspend { newGame(false) }
            is EventToGameScreenViewModel.LoadGame                       -> suspend { newGame(true) }
            is EventToGameScreenViewModel.CloseError                     -> ::closeError
            is EventToGameScreenViewModel.CloseErrorAndFinish            -> ::closeError
            is EventToGameScreenViewModel.OpenGameMenuAndSetLoadingState -> suspend { openGameMenu(setLoadingState = true) }
            is EventToGameScreenViewModel.OpenGameMenuAndSetIdleState    -> suspend { openGameMenu(setLoadingState = false) }
            is EventToGameScreenViewModel.SetIdleState                   -> ::setIdleState
            is EventToGameScreenViewModel.CloseGameMenu                  -> suspend { closeGameMenu() }
            is EventToGameScreenViewModel.GoToMainMenu                   -> ::goToMainMenu
            is EventToGameScreenViewModel.RemoveFlaggedBombs             -> ::removeFlaggedBombs
            is EventToGameScreenViewModel.RemoveOpenedSlices             -> ::removeOpenedSlices
            is EventToGameScreenViewModel.ToggleFlagging                 -> ::toggleFlagging
            is EventToGameScreenViewModel.CloseGameStatusDialog          -> ::closeGameStatusDialog
            is EventToGameScreenViewModel.Finish                         -> ::finish
            else                                                         -> null
        }

        return if (eventProcessor == null) {
            EventProcessingResult.Unprocessed()
        } else {
            eventProcessor()
        }
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

    private suspend fun doActionIfDataIsCorrect(
        isDataCorrect: (gameScreeData: GameScreenData) -> Boolean,
        errorMessage: String,
        silent: Boolean = false,
        action: suspend (gameScreenData: GameScreenData) -> Unit
    ) {
        val gameScreenData = stateHolder.getCurrentData()

        if (!isDataCorrect(gameScreenData)) {
            if (!silent) {
                stateHolder.publishErrorState(errorMessage)
            }
        } else {
            action.invoke(gameScreenData)
        }
    }

    private suspend inline fun <reified T: GameScreenData> doActionIfDataIsChildOf(
        errorMessage: String,
        silent: Boolean = false,
        noinline action: suspend (gameScreenData: T) -> Unit
    ) {
        doActionIfDataIsCorrect(
            { it is T },
            errorMessage,
            silent = silent,
            { gameScreenData -> action.invoke(gameScreenData as T)  }
        )
    }

    private suspend fun newGame(
        loadGame: Boolean
    ): EventProcessingResult<EventToGameScreenViewModel> {
        doActionIfDataIsCorrect(
            { it is GameScreenData.GameMenu },
            "main menu is not opened",
            true
        ) { gameScreenData ->
            tryUnstackState(gameScreenData)
        }

        doActionIfDataIsCorrect(
            { it is GameScreenData.GameInProgress },
            "game is in progress",
            true
        ) {
            stateHolder.publishIdleState(GameScreenData.NoData)
        }

        val timeSpanComponent = gameScreenComponent
            .timeSpanComponent
        val touchListenerComponent = gameScreenComponent
            .touchListenerComponent
        val gameComponent = gameScreenComponent
            .gameComponent

        timeSpanComponent
            .manuallyUpdatableTimeAfterDeviceStartupFlowHolder
            .tick()

        gameScreenComponent
            .gLESRenderer
            .openGLEventsHandler = null

        gameComponent.let { gC ->
            gameControlsImp = gC.gameControlsImp

            uiGameControlsMutableFlows = gC.uiGameControlsMutableFlows
            uiGameControlsFlows = gC.uiGameControlsFlows

            touchListenerComponent.touchListener.bindHandlers(
                gC.touchHandlerImp,
                gC.moveHandlerImp
            )
        }

        gameComponent.minesweeper.eventHandler.handleEventWithBlocking(
            if (loadGame) {
                EventToMinesweeper.LoadGame
            } else {
                EventToMinesweeper.NewGame
            }
        )

        gameScreenComponent.gLESRenderer.openGLEventsHandler =
            gameComponent.minesweeper.openGLEventsHandler


        setFlagging(loadGame)

        stateHolder.publishIdleState(
            GameScreenData.GameInProgress(
                uiGameControlsFlows!!
            )
        )
        return EventProcessingResult.Processed()
    }

    private suspend fun openGameMenu(
        setLoadingState: Boolean = false
    ): EventProcessingResult<EventToGameScreenViewModel> {
        doActionIfDataIsCorrect(
            { it !is GameScreenData.GameMenu },
            "can not open menu twice sequentially"
        ) { gameScreenData ->
            val newScreenData = GameScreenData.GameMenu(
                gameScreenData
            )

            if (setLoadingState) {
                stateHolder.publishLoadingState(
                    newScreenData
                )
            } else {
                stateHolder.publishIdleState(
                    newScreenData
                )
            }
        }
        return EventProcessingResult.Processed()
    }

    private suspend fun setIdleState(
    ): EventProcessingResult<EventToGameScreenViewModel> {
        stateHolder.publishIdleState()
        return EventProcessingResult.Processed()
    }

    private suspend fun tryUnstackState(
        gameScreenData: GameScreenData
    ) {
        val prevData = (gameScreenData as? GameScreenData.HasPrevData)?.prevData

        if (prevData == null) {
            stateHolder.publishErrorState(
                "critical error. reloading",
                GameScreenData.NoData,
            )
            return
        }

        stateHolder.publishIdleState(
            prevData
        )
    }

    private suspend fun closeGameMenu(
        silent: Boolean = false
    ): EventProcessingResult<EventToGameScreenViewModel> {
        doActionIfDataIsCorrect(
            { it is GameScreenData.GameMenu },
            "main menu is not opened",
            silent
        ) { gameScreenData ->
            tryUnstackState(gameScreenData)
        }
        return EventProcessingResult.Processed()
    }

    private suspend fun goToMainMenu(
    ): EventProcessingResult<EventToGameScreenViewModel> {
        closeGameMenu(true)
        return EventProcessingResult.PushNewEvent(
            EventToGameScreenViewModel.Finish
        )
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

    private suspend fun skipIfGameIsNotInProgress(
        action: suspend (gameInProgress: GameScreenData.GameInProgress) -> Unit
    ) {
        doActionIfDataIsChildOf(
            "game is not in progress",
            true,
            action
        )
        setIdleState()
    }

    private suspend fun removeFlaggedBombs(
    ): EventProcessingResult<EventToGameScreenViewModel> {
        skipIfGameIsNotInProgress {
            gameControlsImp?.removeFlaggedCells = true
        }
        return EventProcessingResult.Processed()
    }

    private suspend fun removeOpenedSlices(
    ): EventProcessingResult<EventToGameScreenViewModel> {
        skipIfGameIsNotInProgress {
            gameControlsImp?.removeOpenedSlices = true
        }
        return EventProcessingResult.Processed()
    }

    private suspend fun toggleFlagging(
    ): EventProcessingResult<EventToGameScreenViewModel> {
        skipIfGameIsNotInProgress {
            gameControlsImp?.let {
                setFlagging(
                    !it.flagging
                )
            }
        }
        return EventProcessingResult.Processed()
    }

    private fun setFlagging(
        newVal: Boolean
    ) {
        gameControlsImp?.flagging = newVal
        uiGameControlsMutableFlows?.flagging?.value = newVal
    }

    private suspend fun closeGameStatusDialog(
    ): EventProcessingResult<EventToGameScreenViewModel> {
        uiGameControlsMutableFlows?.uiGameStatus?.value = UIGameStatus.Unimportant
        return EventProcessingResult.Processed()
    }
}

