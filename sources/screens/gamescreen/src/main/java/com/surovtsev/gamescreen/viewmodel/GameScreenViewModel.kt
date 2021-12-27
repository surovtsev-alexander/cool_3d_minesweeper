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
import com.surovtsev.gamelogic.dagger.DaggerGameComponent
import com.surovtsev.gamelogic.dagger.GameComponent
import com.surovtsev.gamelogic.minesweeper.interaction.commandhandler.CommandToMinesweeper
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.dagger.DaggerGameScreenComponent
import com.surovtsev.gamescreen.dagger.GameScreenComponent
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
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

    private val gameScreenComponent: GameScreenComponent by lazy {
        DaggerGameScreenComponent.create()
    }

    private val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent by lazy {
        DaggerRestartableCoroutineScopeComponent
            .create()
    }

    private val timeSpanComponent: TimeSpanComponent by lazy {
        DaggerTimeSpanComponent
            .builder()
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "GameScreenViewModel:TimeSpanComponent"
                )
            )
            .build()
    }

    private val touchListenerComponent: TouchListenerComponent by lazy {
        DaggerTouchListenerComponent
            .builder()
            .timeSpanComponentEntryPoint(
                timeSpanComponent
            )
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "GameScreenViewModel:TouchListener"
                )
            )
            .build()
    }

    // look ::onDestroy
    @SuppressLint("StaticFieldLeak")
    private var gLSurfaceView: GLSurfaceView? = null

    private var gameComponent: GameComponent? = null

    private var gameControlsImp: GameControlsImp? = null
    private var uiGameControlsMutableFlows: UIGameControlsMutableFlows? = null
    private var uiGameControlsFlows: UIGameControlsFlows? = null

    override fun onCreate(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onCreate(owner)
        restartableCoroutineScopeComponent.subscriberImp.restart()
    }

    override fun onResume(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onResume(owner)
        gLSurfaceView?.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onPause(owner)
        gLSurfaceView?.onPause()

        pauseGame()
        gameComponent?.minesweeper?.commandHandler?.handleCommandWithBlocking(
            CommandToMinesweeper.SaveGame
        )

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
        restartableCoroutineScopeComponent
            .subscriberImp
            .onStop()

        gameScreenComponent.gLESRenderer.openGLEventsHandler = null

        publishIdleState(
            GameScreenData.NoData
        )
    }

    override suspend fun getCommandProcessor(command: CommandFromGameScreen): CommandProcessor? {
        return when (command) {
            is CommandFromGameScreen.HandleScreenLeaving    -> suspend { handleScreenLeaving(command.owner) }
            is CommandFromGameScreen.NewGame                -> suspend { newGame(false) }
            is CommandFromGameScreen.LoadGame               -> suspend { newGame(true) }
            is CommandFromGameScreen.CloseError             -> ::closeError
            is CommandFromGameScreen.CloseErrorAndFinish    -> ::closeError
            is CommandFromGameScreen.OpenGameMenu           -> ::openGameMenu
            is CommandFromGameScreen.CloseGameMenu          -> suspend { closeGameMenu() }
            is CommandFromGameScreen.GoToMainMenu           -> ::goToMainMenu
            is CommandFromGameScreen.RemoveFlaggedBombs     -> ::removeFlaggedBombs
            is CommandFromGameScreen.RemoveOpenedSlices     -> ::removeOpenedSlices
            is CommandFromGameScreen.ToggleFlagging         -> ::toggleFlagging
            is CommandFromGameScreen.CloseGameStatusDialog  -> ::closeGameStatusDialog
            else                                            -> null
        }
    }

    fun initGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        val gameRenderer = gameScreenComponent.gLESRenderer
        val touchListener = touchListenerComponent.touchListener

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

        timeSpanComponent.manuallyUpdatableTimeAfterDeviceStartupFlowHolder.tick()
        gameScreenComponent.gLESRenderer.openGLEventsHandler = null

        gameComponent = DaggerGameComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.create(
                    restartableCoroutineScopeComponent,
                    "GameScreenViewModel:GameComponent"
                )
            )
            .timeSpanComponentEntryPoint(timeSpanComponent)
            .cameraInfoHelperHolder(gameScreenComponent)
            .loadGame(loadGame)
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

                // TODO: move updating to Minesweeper.CommandHandler.newGame.
                gameScreenComponent.cameraInfoHelper.also {
                    if (!loadGame) {
                        it.cameraInfo.moveToOrigin()
                    }
                    it.update()
                }
            }

        setFlagging(loadGame)

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

        pauseGame()

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
            it.minesweeper.gameLogicStateHelper.pauseIfNeeded()
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

    private suspend fun removeOpenedSlices() {
        skipIfGameIsNotInProgress {
            gameControlsImp?.removeOpenedSlices = true
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

