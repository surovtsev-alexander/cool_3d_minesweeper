package com.surovtsev.game.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.helpers.sorting.RankingTableColumn
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.helpers.sorting.SortDirection
import com.surovtsev.core.viewmodel.CommandProcessor
import com.surovtsev.core.viewmodel.ScreenCommandHandler
import com.surovtsev.core.viewmodel.ScreenStateValue
import com.surovtsev.core.viewmodel.TemplateScreenViewModel
import com.surovtsev.game.dagger.DaggerGameComponent
import com.surovtsev.game.dagger.GameComponent
import com.surovtsev.game.viewmodel.helpers.Place
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.touchlistener.dagger.DaggerTouchListenerComponent
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import logcat.logcat

const val LoadGameParameterName = "load_game"

typealias GameScreenStateHolder = MutableLiveData<GameScreenState>
typealias GameScreenStateValue = ScreenStateValue<GameScreenData>

typealias GameScreenCommandHandler = ScreenCommandHandler<CommandFromGameScreen>

typealias GLSurfaceViewCreated = (gLSurfaceView: GLSurfaceView) -> Unit

class GameScreenViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel<CommandFromGameScreen, GameScreenData>(
        CommandFromGameScreen.LoadGame,
        { CommandFromGameScreen.HandleScreenLeaving(it) },
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
    var gLSurfaceView: GLSurfaceView? = null

    var timeSpanComponent: TimeSpanComponent? = null
    var gameComponent: GameComponent? = null
        private set
    private var touchListenerComponent: TouchListenerComponent? = null

    override fun onResume(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onResume(owner)
        gLSurfaceView?.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super<TemplateScreenViewModel>.onPause(owner)
        gLSurfaceView?.onPause()

        pauseGame()
        gameComponent?.let {
            it.minesweeperController.storeGameIfNeeded()
        }

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
        noinline action: (gameScreenData: GameScreenData) -> Unit
    ) {
        doActionIfDataIsCorrect(
            { it is T },
            errorMessage,
            silent = false,
            action
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

        timeSpanComponent
            .timeSpan
            .flush()

        publishIdleState(
            GameScreenData.GameInProgress
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
//            markingEvent.onDataChanged(
//                !(markingEvent.valueOrDefault)
//            )
//
//            return true
//        }
//
//        return false
//    }

    fun requestLastWinPlace() {
        launchOnIOThread {
            var res: Place = Place.NoPlace

            val gameComponent = gameComponent
            do {
                if (gameComponent == null) {
                    break
                }

                val settings = gameComponent.settingsDao.getBySettingsData(
                    gameComponent.gameConfig.settingsData
                ) ?: break

                val rankingListHelper = gameComponent.rankingListHelper

                val filteredData = rankingListHelper.createRankingListWithPlaces(
                    settings.id
                )
                val rankingTableSortType = RankingTableSortParameters(
                    RankingTableColumn.SortableTableColumn.DateTableColumn,
                    SortDirection.Descending
                )
                val sortedData = rankingListHelper.sortData(
                    filteredData,
                    rankingTableSortType
                )

                if (sortedData.isEmpty()) {
                    break
                }

                res = Place.WinPlace(sortedData.first().place)
            } while (false)

            withUIContext {
                gameComponent?.let {
                    it.gameScreenEvents.lastWinPlaceEvent.onDataChanged(
                        res
                    )
                }
            }
        }
    }
}

