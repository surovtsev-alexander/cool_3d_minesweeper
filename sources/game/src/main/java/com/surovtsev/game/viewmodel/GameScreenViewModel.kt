package com.surovtsev.game.viewmodel

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import androidx.lifecycle.*
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.helpers.sorting.RankingTableColumn
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.helpers.sorting.SortDirection
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.viewmodel.CommandProcessor
import com.surovtsev.core.viewmodel.ScreenCommandHandler
import com.surovtsev.game.dagger.GameComponent
import com.surovtsev.game.dagger.GameComponentEntryPoint
import com.surovtsev.game.minesweeper.MinesweeperController
import com.surovtsev.game.models.game.config.GameConfig
import com.surovtsev.game.models.game.interaction.GameControls
import com.surovtsev.game.viewmodel.helpers.GameScreenEvents
import com.surovtsev.game.viewmodel.helpers.Place
import com.surovtsev.game.views.glesrenderer.GLESRenderer
import com.surovtsev.touchlistener.TouchListener
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import com.surovtsev.touchlistener.dagger.TouchListenerEntryPoint
import com.surovtsev.utils.timers.TimeSpanHelperImp
import com.surovtsev.core.viewmodel.TemplateScreenViewModel
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import logcat.logcat
import javax.inject.Inject
import javax.inject.Provider

const val LoadGameParameterName = "load_game"

typealias GameScreenStateHolder = MutableLiveData<GameScreenState>
typealias GameScreenStateValue = LiveData<GameScreenState>

typealias GameScreenCommandHandler = ScreenCommandHandler<CommandFromGameScreen>

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    gameComponentProvider: Provider<GameComponent.Builder>,
    touchListenerComponentProvider: Provider<TouchListenerComponent.Builder>,
    savedStateHandle: SavedStateHandle,
    private val timeSpanHelperImp: TimeSpanHelperImp,
):
    TemplateScreenViewModel<CommandFromGameScreen, GameScreenData>(
        CommandFromGameScreen.LoadGame,
        GameScreenData.NoData
    ),
    GameScreenCommandHandler,
    DefaultLifecycleObserver
{
    val loadGame: Boolean = savedStateHandle.get<String>(LoadGameParameterName).toBoolean()

    val minesweeperController: MinesweeperController

    private val gameRenderer: GLESRenderer

    // see ::onDestroy
    @SuppressLint("StaticFieldLeak")
    var gLSurfaceView: GLSurfaceView? = null
    val gameScreenEvents: GameScreenEvents
    val gameControls: GameControls
    private val gameConfig: GameConfig
    private val settingsDao: SettingsDao
    private val rankingDao: RankingDao
    private val rankingListHelper: RankingListHelper
    private val touchListener: TouchListener

    override val stateHolder: GameScreenStateHolder
    override val stateValue: GameScreenStateValue

    init {

        val gameComponent = gameComponentProvider
            .get()
            .loadGame(loadGame)
            .build()
        val gameComponentEntryPoint = EntryPoints.get(
            gameComponent, GameComponentEntryPoint::class.java
        )

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
        gameConfig =
            gameComponentEntryPoint.gameConfig
        settingsDao =
            gameComponentEntryPoint.settingsDao
        rankingDao =
            gameComponentEntryPoint.rankingDao
        rankingListHelper =
            gameComponentEntryPoint.rankingListHelper

        stateHolder =
            gameComponentEntryPoint.gameScreenStateHolder
        stateValue =
            gameComponentEntryPoint.gameScreenStateValue

        val touchListenerComponent = touchListenerComponentProvider
            .get()
            .touchHandler(
                gameComponentEntryPoint.touchHandlerImp)
            .moveHandler(
                gameComponentEntryPoint.moveHandlerImp)
            .timeSpanHelper(
                gameComponentEntryPoint.timeSpanHelperImp
            )
            .build()

        val touchListenerEntryPoint = EntryPoints.get(
            touchListenerComponent, TouchListenerEntryPoint::class.java
        )

        touchListener = touchListenerEntryPoint.touchListener
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        gLSurfaceView!!.apply {
            /* TODO. add back */
            touchListener.connectToGLSurfaceView(
                this
            )

            setEGLContextClientVersion(2)
            setRenderer(gameRenderer)
        }
    }

    override suspend fun getCommandProcessor(command: CommandFromGameScreen): CommandProcessor? {
        return when (command) {
            is CommandFromGameScreen.NewGame       -> ::newGame
            is CommandFromGameScreen.LoadGame      -> ::loadGame
            is CommandFromGameScreen.CloseError    -> ::closeError
            is CommandFromGameScreen.Pause         -> ::pause
            is CommandFromGameScreen.Resume        -> ::resume
            is CommandFromGameScreen.OpenMenu      -> ::openMenu
            is CommandFromGameScreen.CloseMenu     -> ::closeMenu
            is CommandFromGameScreen.GoToMainMenu  -> ::goToMainMenu
            else                                   -> null
        }
    }

    private suspend fun doActionIfDataIsCorrect(
        isDataCorrect: (gameScreeData: GameScreenData) -> Boolean,
        errorMessage: String,
        action: suspend (gameScreenData: GameScreenData) -> Unit
    ) {
        val gameScreenData = stateHolder.value?.screenData

        if (gameScreenData == null || !isDataCorrect(gameScreenData)) {
            publishErrorState(errorMessage)
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
            action
        )
    }

    private suspend fun newGame() {

    }

    private suspend fun loadGame() {

    }

    private suspend fun pause() {

    }

    private suspend fun resume() {

    }

    private suspend fun openMenu() {
        logcat { "open menu" }
        doActionIfDataIsCorrect(
            { it !is GameScreenData.MainMenu },
            "can not open menu twice sequentially",
        ) { gameScreenData ->
            publishIdleState(
                GameScreenData.MainMenu(
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

    private suspend fun closeMenu() {
        doActionIfDataIsCorrect(
            { it is GameScreenData.MainMenu },
            "main menu is not opened"
        ) { gameScreenData ->
            tryUnstackState(gameScreenData)
        }
    }

    private suspend fun goToMainMenu() {
        withUIContext {
            finishAction?.invoke()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        gLSurfaceView!!.onPause()
        minesweeperController.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        gLSurfaceView!!.onResume()
        minesweeperController.onResume(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        minesweeperController.onDestroy(owner)
        timeSpanHelperImp.forgetSubscribers()

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

            do {
                val settings = settingsDao.getBySettingsData(
                    gameConfig.settingsData
                ) ?: break

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
                gameScreenEvents.lastWinPlaceEvent.onDataChanged(
                    res
                )
            }
        }
    }
}
