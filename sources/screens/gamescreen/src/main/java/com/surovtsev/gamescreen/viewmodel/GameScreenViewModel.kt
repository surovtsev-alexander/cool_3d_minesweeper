package com.surovtsev.gamescreen.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.viewmodel.*
import com.surovtsev.gamescreen.dagger.DaggerGameComponent
import com.surovtsev.gamescreen.dagger.GameComponent
import com.surovtsev.gamescreen.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.viewmodel.helpers.UIGameControlsFlows
import com.surovtsev.gamescreen.viewmodel.helpers.UIGameControlsMutableFlows
import com.surovtsev.gamescreen.viewmodel.helpers.UIGameStatus
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.touchlistener.dagger.DaggerTouchListenerComponent
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

const val LoadGameParameterName = "load_game"

typealias GameScreenStateHolder = MutableLiveData<GameScreenState>
typealias GameScreenStateValue = ScreenStateValue<GameScreenData>

typealias GameScreenCommandHandler = ScreenCommandHandler<CommandFromGameScreen>

typealias GLSurfaceViewCreated = (gLSurfaceView: GLSurfaceView) -> Unit

typealias GameScreenErrorDialogPlacer = ErrorDialogPlacer<CommandFromGameScreen, GameScreenData>

class GameScreenViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel<CommandFromGameScreen, GameScreenData>(
        CommandFromGameScreen.BaseCommands,
        GameScreenData.NoData,
        GameScreenStateHolder(GameScreenInitialState)
    ),
    GameScreenCommandHandler,
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<GameScreenViewModel>

    // look ::onDestroy
    @SuppressLint("StaticFieldLeak")
    private var gLSurfaceView: GLSurfaceView? = null

    private var timeSpanComponent: TimeSpanComponent? = null
    private var gameComponent: GameComponent? = null
    private var touchListenerComponent: TouchListenerComponent? = null

    private var gameControlsImp: GameControlsImp? = null
    private var uiGameControlsMutableFlows: UIGameControlsMutableFlows? = null
    private var uiGameControlsFlows: UIGameControlsFlows? = null

    override fun onResume(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onResume(owner)
        gLSurfaceView?.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onPause(owner)
        gLSurfaceView?.onPause()

        pauseGame()
        gameComponent?.minesweeperController?.storeGameIfNeeded()

        if (state.value?.screenData !is GameScreenData.GameMenu) {
            handleCommand(
                CommandFromGameScreen.OpenGameMenu
            )
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onDestroy(owner)

        gLSurfaceView = null
    }

    override suspend fun handleScreenLeaving(
        owner: LifecycleOwner
    ) {
        handleCommand(
            CommandFromGameScreen.CloseGame
        )
    }

    override suspend fun getCommandProcessor(command: CommandFromGameScreen): CommandProcessor? {
        return when (command) {
            is CommandFromGameScreen.HandleScreenLeaving    -> suspend { handleScreenLeaving(command.owner) }
            is CommandFromGameScreen.CloseGame              -> ::closeGame
            is CommandFromGameScreen.NewGame                -> suspend { newGame(false) }
            is CommandFromGameScreen.LoadGame               -> suspend { newGame(true) }
            is CommandFromGameScreen.CloseError             -> ::closeError
            is CommandFromGameScreen.CloseErrorAndFinish    -> ::closeError
            is CommandFromGameScreen.OpenGameMenu           -> ::openGameMenu
            is CommandFromGameScreen.CloseGameMenu          -> suspend { closeGameMenu() }
            is CommandFromGameScreen.GoToMainMenu           -> ::goToMainMenu
            is CommandFromGameScreen.RemoveFlaggedBombs     -> ::removeFlaggedBombs
            is CommandFromGameScreen.RemoveOpenedBorders    -> ::removeOpenedBorders
            is CommandFromGameScreen.ToggleFlagging         -> ::toggleFlagging
            is CommandFromGameScreen.CloseGameStatusDialog  -> ::closeGameStatusDialog
            else                                            -> null
        }
    }

    private suspend fun closeGame() {
        timeSpanComponent
            ?.subscriberImp
            ?.onStop()

        publishIdleState(
            GameScreenData.NoData
        )
    }

    fun initGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        val gameRenderer = gameComponent?.gameRenderer
        val touchListener = touchListenerComponent?.touchListener

        if (gameRenderer == null || touchListener == null) {
            return
        }

        gLSurfaceView.setEGLContextClientVersion(2)
        gLSurfaceView.setRenderer(gameRenderer)
        touchListener.connectToGLSurfaceView(
            gLSurfaceView)

        this.gLSurfaceView = gLSurfaceView
    }

    private suspend fun doActionIfDataIsCorrect(
        isDataCorrect: (gameScreeData: GameScreenData) -> Boolean,
        errorMessage: String,
        silent: Boolean = false,
        action: suspend (gameScreenData: GameScreenData) -> Unit
    ) {
        val gameScreenData = state.value?.screenData

        if (gameScreenData == null || !isDataCorrect(gameScreenData)) {
            if (!silent) {
                publishErrorState(errorMessage)
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

    private suspend fun newGame(loadGame: Boolean) {
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
            publishIdleState(GameScreenData.NoData)
        }

        val timeSpanComponent = DaggerTimeSpanComponent
            .create()
        this.timeSpanComponent = timeSpanComponent

        gameComponent = DaggerGameComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .timeSpanComponentEntryPoint(timeSpanComponent)
            .loadGame(loadGame)
            .build()
            .also {
                gameControlsImp = it.gameControlsImp

                uiGameControlsMutableFlows = it.uiGameControlsMutableFlows
                uiGameControlsFlows = it.uiGameControlsFlows

                touchListenerComponent = DaggerTouchListenerComponent
                    .builder()
                    .timeSpanComponentEntryPoint(
                        timeSpanComponent
                    )
                    .touchHandler(
                        it.touchHandlerImp
                    )
                    .moveHandler(
                        it.moveHandlerImp
                    )
                    .build()
            }

        setFlagging(loadGame)

        timeSpanComponent
            .timeSpan
            .flush()

        publishIdleState(
            GameScreenData.GameInProgress(
                uiGameControlsFlows!!
            )
        )
    }

    private suspend fun openGameMenu() {
        doActionIfDataIsCorrect(
            { it !is GameScreenData.GameMenu },
            "can not open menu twice sequentially"
        ) { gameScreenData ->

            pauseGame()

            publishIdleState(
                GameScreenData.GameMenu(
                    gameScreenData
                )
            )
        }
    }

    private suspend fun tryUnstackState(
        gameScreenData: GameScreenData
    ) {
        val prevData = (gameScreenData as? GameScreenData.HasPrevData)?.prevData

        if (prevData == null) {
            publishErrorState(
                "critical error. reloading",
                GameScreenData.NoData,
            )
            return
        }

        publishIdleState(
            prevData
        )
    }

    private suspend fun closeGameMenu(silent: Boolean = false) {

        gameComponent?.let {
            it.minesweeperController.gameLogic.gameLogicStateHelper.resumeIfNeeded()
        }

        doActionIfDataIsCorrect(
            { it is GameScreenData.GameMenu },
            "main menu is not opened",
            silent
        ) { gameScreenData ->
            tryUnstackState(gameScreenData)
        }
    }

    private suspend fun goToMainMenu() {
        closeGameMenu(true)
        withUIContext {
            finishAction?.invoke()
        }
    }

    private fun pauseGame() {
        gameComponent?.let {
            it.minesweeperController.gameLogic.gameLogicStateHelper.pauseIfNeeded()
        }
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
    }

    private suspend fun removeFlaggedBombs() {
        skipIfGameIsNotInProgress {
            gameControlsImp?.removeFlaggedCells = true
        }
    }

    private suspend fun removeOpenedBorders() {
        skipIfGameIsNotInProgress {
            gameControlsImp?.removeOpenedBorders = true
        }
    }

    private suspend fun toggleFlagging() {
        skipIfGameIsNotInProgress {
            gameControlsImp?.let {
                setFlagging(
                    !it.flagging
                )
            }
        }
    }

    private fun setFlagging(
        newVal: Boolean
    ) {
        gameControlsImp?.flagging = newVal
        uiGameControlsMutableFlows?.flagging?.value = newVal
    }

    private fun closeGameStatusDialog() {
        uiGameControlsMutableFlows?.uiGameStatus?.value = UIGameStatus.Unimportant
    }
}

