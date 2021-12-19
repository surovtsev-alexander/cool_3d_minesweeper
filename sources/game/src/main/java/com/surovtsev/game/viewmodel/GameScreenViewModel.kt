package com.surovtsev.game.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.*
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.helpers.sorting.RankingTableColumn
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.helpers.sorting.SortDirection
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.viewmodel.CommandProcessor
import com.surovtsev.core.viewmodel.ScreenCommandHandler
import com.surovtsev.core.viewmodel.TemplateScreenViewModel
import com.surovtsev.game.dagger.DaggerGameComponent
import com.surovtsev.game.minesweeper.MinesweeperController
import com.surovtsev.game.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.game.models.game.config.GameConfig
import com.surovtsev.game.models.game.interaction.GameControls
import com.surovtsev.game.viewmodel.helpers.GameScreenEvents
import com.surovtsev.game.viewmodel.helpers.Place
import com.surovtsev.game.views.glesrenderer.GLESRenderer
import com.surovtsev.touchlistener.TouchListener
import com.surovtsev.touchlistener.dagger.DaggerTouchListenerComponent
import com.surovtsev.utils.timers.TimeSpanFlow
import com.surovtsev.utils.timers.TimeSpanHelperImp
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import logcat.logcat

const val LoadGameParameterName = "load_game"

typealias GameScreenStateHolder = MutableLiveData<GameScreenState>
typealias GameScreenStateValue = LiveData<GameScreenState>

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

    val minesweeperController: MinesweeperController
    private val gameRenderer: GLESRenderer
    val gameScreenEvents: GameScreenEvents
    val gameControls: GameControls
    private val gameConfig: GameConfig
    private val settingsDao: SettingsDao
    private val rankingListHelper: RankingListHelper
    private val touchListener: TouchListener
    val bombsLeftFlow: BombsLeftFlow
    val timeSpanFlow: TimeSpanFlow
    private val timeSpanHelperImp: TimeSpanHelperImp

    override val stateHolder: GameScreenStateHolder
    override val stateValue: GameScreenStateValue

    init {
        val loadGame: Boolean = savedStateHandle.get<String>(LoadGameParameterName).toBoolean()
        val gameComponent = DaggerGameComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .loadGame(loadGame)
            .build()

        minesweeperController =
            gameComponent.minesweeperController
        gameRenderer =
            gameComponent.gameRenderer
        gameScreenEvents =
            gameComponent.gameScreenEvents
        gameControls =
            gameComponent.gameControls
        gameConfig =
            gameComponent.gameConfig
        settingsDao =
            gameComponent.settingsDao
        rankingListHelper =
            gameComponent.rankingListHelper
        bombsLeftFlow =
            gameComponent.bombsLeftFlow
        timeSpanFlow =
            gameComponent.timeSpan.timeSpanFlow
        timeSpanHelperImp =
            gameComponent.timeSpanHelperImp

        stateHolder =
            gameComponent.gameScreenStateHolder
        stateValue =
            gameComponent.gameScreenStateValue

        val touchListenerComponent = DaggerTouchListenerComponent
            .builder()
            .touchHandler(
                gameComponent.touchHandlerImp)
            .moveHandler(
                gameComponent.moveHandlerImp)
            .timeSpanHelper(
                gameComponent.timeSpanHelperImp
            )
            .build()

        touchListener = touchListenerComponent.touchListener
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override suspend fun getCommandProcessor(command: CommandFromGameScreen): CommandProcessor? {
        return when (command) {
            is CommandFromGameScreen.NewGame            -> suspend { newGame(false) }
            is CommandFromGameScreen.LoadGame           -> suspend { newGame(true) }
            is CommandFromGameScreen.CloseError         -> ::closeError
            is CommandFromGameScreen.Pause              -> ::pause
            is CommandFromGameScreen.Resume             -> ::resume
            is CommandFromGameScreen.OpenMenu           -> ::openMenu
            is CommandFromGameScreen.CloseMenu          -> ::closeMenu
            is CommandFromGameScreen.GoToMainMenu       -> ::goToMainMenu
            else                                        -> null
        }
    }

    fun initGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        this.gLSurfaceView = gLSurfaceView

        gLSurfaceView.setEGLContextClientVersion(2)
        gLSurfaceView.setRenderer(gameRenderer)
        touchListener.connectToGLSurfaceView(
            gLSurfaceView
        )
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

    private suspend fun newGame(loadGame: Boolean) {
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
        gLSurfaceView?.onPause()
        minesweeperController.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        gLSurfaceView?.onResume()
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
