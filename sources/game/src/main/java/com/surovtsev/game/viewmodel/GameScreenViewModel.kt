package com.surovtsev.game.viewmodel

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.helpers.sorting.RankingTableColumn
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.helpers.sorting.SortDirection
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
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
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

const val LoadGameParameterName = "load_game"

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class GameScreenViewModel @Inject constructor(
    gameComponentProvider: Provider<GameComponent.Builder>,
    touchListenerComponentProvider: Provider<TouchListenerComponent.Builder>,
    savedStateHandle: SavedStateHandle,
    private val timeSpanHelperImp: TimeSpanHelperImp,
):
    ViewModel(),
    DefaultLifecycleObserver,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{
    val minesweeperController: MinesweeperController

    private val gameRenderer: GLESRenderer
    val gLSurfaceView: GLSurfaceView
    val gameScreenEvents: GameScreenEvents
    val gameControls: GameControls
    private val gameConfig: GameConfig
    private val settingsDao: SettingsDao
    private val rankingDao: RankingDao
    private val rankingListHelper: RankingListHelper
    private val touchListener: TouchListener

    init {

        val loadGame = savedStateHandle.get<String>(LoadGameParameterName).toBoolean()

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
        gLSurfaceView.apply {
            /* TODO. add back */
            touchListener.connectToGLSurfaceView(
                gLSurfaceView
            )

            setEGLContextClientVersion(2)
            setRenderer(gameRenderer)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        gLSurfaceView.onPause()
        minesweeperController.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        gLSurfaceView.onResume()
        minesweeperController.onResume(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        minesweeperController.onDestroy(owner)
        timeSpanHelperImp.forgetSubscribers()
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
