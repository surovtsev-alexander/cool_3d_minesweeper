package com.surovtsev.gamescreen.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.dependencies.GameStateDependencies
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.viewmodel.*
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.gamelogic.dagger.DaggerGameComponent
import com.surovtsev.gamelogic.dagger.GameComponent
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventToMinesweeper
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.viewmodel.helpers.DaggerComponentsHolder
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

    private val daggerComponentsHolder = DaggerComponentsHolder()

    // look ::onDestroy
    @SuppressLint("StaticFieldLeak")
    private var gLSurfaceView: GLSurfaceView? = null

    private var gameComponent: GameComponent? = null

    private var gameControlsImp: GameControlsImp? = null
    private var uiGameControlsMutableFlows: UIGameControlsMutableFlows? = null
    private var uiGameControlsFlows: UIGameControlsFlows? = null

    override fun onCreate(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onCreate(owner)
        daggerComponentsHolder
            .restartableCoroutineScopeComponentHolder
            .getOrCreate {
                it.subscriberImp.restart()
            }
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

        gameComponent?.minesweeper?.eventHandler?.handleEventWithBlocking(
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
        daggerComponentsHolder
            .restartableCoroutineScopeComponentHolder
            .component
            ?.subscriberImp
            ?.onStop()

        daggerComponentsHolder
            .gameScreenComponentHolder
            .component
            ?.gLESRenderer
            ?.openGLEventsHandler = null

        stateHolder.publishIdleState(
            GameScreenData.NoData
        )

        return EventProcessingResult.Processed()
    }

    override suspend fun getEventProcessor(event: EventToGameScreenViewModel): EventProcessor<EventToGameScreenViewModel>? {
        return when (event) {
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
            else                                                    -> null
        }
    }

    fun initGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        val gameRenderer = daggerComponentsHolder
            .gameScreenComponentHolder
            .getOrCreate()
            .gLESRenderer
        val touchListener = daggerComponentsHolder
            .touchListenerComponentHolder
            .getOrCreate()
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

        val timeSpanComponent = daggerComponentsHolder
            .timeSpanComponentHolder
            .getOrCreate()
        val restartableCoroutineScopeComponent =
            daggerComponentsHolder
                .restartableCoroutineScopeComponentHolder
                .getOrCreate()
        val gameScreenComponent =
            daggerComponentsHolder
                .gameScreenComponentHolder
                .getOrCreate()
        val touchListenerComponent =
            daggerComponentsHolder
                .touchListenerComponentHolder
                .getOrCreate()

        timeSpanComponent
            .manuallyUpdatableTimeAfterDeviceStartupFlowHolder
            .tick()

        gameScreenComponent
            .gLESRenderer
            .openGLEventsHandler = null

        val gameNotPausedFlow = stateHolder.state.map { screenState ->
            screenState.description is StateDescription.Idle &&
            screenState.data is GameScreenData.GameInProgress
        }.stateIn(
            restartableCoroutineScopeComponent
                .customCoroutineScope
        )

        gameComponent = DaggerGameComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .gameScreenEntryPoint(gameScreenComponent)
            .restartableCoroutineScopeEntryPoint(restartableCoroutineScopeComponent)
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.create(
                    restartableCoroutineScopeComponent,
                    "GameScreenViewModel:GameComponent"
                )
            )
            .timeSpanComponentEntryPoint(timeSpanComponent)
            .loadGame(loadGame)
            .gameStateDependencies(GameStateDependencies(
                appComponentEntryPoint,
                timeSpanComponent,
                loadGame
            ))
            .gameNotPausedFlow(gameNotPausedFlow)
            .build()
            .also { gC ->
                gameControlsImp = gC.gameControlsImp

                uiGameControlsMutableFlows = gC.uiGameControlsMutableFlows
                uiGameControlsFlows = gC.uiGameControlsFlows

                touchListenerComponent.touchListener.bindHandlers(
                    gC.touchHandlerImp,
                    gC.moveHandlerImp
                )

                gameScreenComponent.gLESRenderer.openGLEventsHandler =
                    gC.minesweeper.openGLEventsHandler

                // TODO: move updating to Minesweeper.EventHandler.newGame.
                gC.cameraInfoHelperHolder.cameraInfoHelperFlow.value.also {
                    if (!loadGame) {
                        it.cameraInfo.moveToOrigin()
                    }
                    it.update()
                }
            }

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
        withUIContext {
            finishAction?.invoke()
        }
        return EventProcessingResult.Processed()
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

    private fun closeGameStatusDialog(
    ): EventProcessingResult<EventToGameScreenViewModel> {
        uiGameControlsMutableFlows?.uiGameStatus?.value = UIGameStatus.Unimportant
        return EventProcessingResult.Processed()
    }
}

