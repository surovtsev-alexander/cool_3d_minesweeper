package com.surovtsev.game.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.*
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
        GameScreenData.NoData
    ),
    GameScreenCommandHandler,
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<GameScreenViewModel>

    // see ::onDestroy
    @SuppressLint("StaticFieldLeak")
    var gLSurfaceView: GLSurfaceView? = null

    override val stateHolder: GameScreenStateHolder
    override val stateValue: GameScreenStateValue

    var gameComponent: GameComponent? = null
        private set
    var touchListenerComponent: TouchListenerComponent? = null
        private set

    init {
        val loadGame: Boolean = savedStateHandle.get<String>(LoadGameParameterName).toBoolean()
        gameComponent = DaggerGameComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .loadGame(loadGame)
            .build()
            .also {
                stateHolder = it.gameScreenStateHolder
                stateValue = it.gameScreenStateValue


                touchListenerComponent = DaggerTouchListenerComponent
                    .builder()
                    .touchHandler(
                        it.touchHandlerImp
                    )
                    .moveHandler(
                        it.moveHandlerImp
                    )
                    .timeSpanHelper(
                        it.timeSpanHelperImp
                    )
                    .build()
            }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override suspend fun getCommandProcessor(command: CommandFromGameScreen): CommandProcessor? {
        return when (command) {
            is CommandFromGameScreen.NewGame                -> suspend { newGame(false) }
            is CommandFromGameScreen.LoadGame               -> suspend { newGame(true) }
            is CommandFromGameScreen.CloseError             -> ::closeError
            is CommandFromGameScreen.CloseErrorAndFinish    -> ::closeError
            is CommandFromGameScreen.Pause                  -> ::pause
            is CommandFromGameScreen.Resume                 -> ::resume
            is CommandFromGameScreen.OpenMenu               -> ::openMenu
            is CommandFromGameScreen.CloseMenu              -> ::closeMenu
            is CommandFromGameScreen.GoToMainMenu           -> ::goToMainMenu
            else                                            -> null
        }
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
        val gameScreenData = stateHolder.value?.screenData

        if (gameScreenData == null || !isDataCorrect(gameScreenData)) {
            if (silent) {
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
        publishIdleState(
            GameScreenData.GameInProgress
        )
//        val gameLogicComponent = gameLogicComponentProvider
//            .get()
////            .loadGame(loadGame)
//            .build()
//        val gameLogicComponentEntryPoint = EntryPoints.get(
//            gameLogicComponent,
//            GameLogicComponentEntryPoint::class.java
//        )


    }

    private suspend fun pause() {
    }

    private suspend fun resume() {
    }

    private suspend fun openMenu() {
        logcat { "open menu" }
        doActionIfDataIsCorrect(
            { it !is GameScreenData.GameMenu },
            "can not open menu twice sequentially"
        ) { gameScreenData ->
            publishIdleState(
                GameScreenData.GameMenu(
                    gameScreenData
                )
            )
            logcat { "done" }
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

    private suspend fun closeMenu(silent: Boolean = false) {
        doActionIfDataIsCorrect(
            { it is GameScreenData.GameMenu },
            "main menu is not opened",
            silent
        ) { gameScreenData ->
            tryUnstackState(gameScreenData)
        }
    }

    private suspend fun goToMainMenu() {
        closeMenu(true)
        withUIContext {
            finishAction?.invoke()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        gLSurfaceView?.onPause()
        gameComponent?.minesweeperController?.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        gLSurfaceView?.onResume()
        gameComponent?.minesweeperController?.onResume(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        gameComponent?.apply {
            minesweeperController.onDestroy(owner)
            timeSpanHelperImp.forgetSubscribers()
        }
        gLSurfaceView = null
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

