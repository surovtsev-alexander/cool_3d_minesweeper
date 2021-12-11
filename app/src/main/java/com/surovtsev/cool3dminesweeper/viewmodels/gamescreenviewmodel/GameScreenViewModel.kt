package com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool3dminesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool3dminesweeper.dagger.app.game.GameComponentEntryPoint
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.interaction.GameControls
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.presentation.gamescreen.LoadGameParameterName
import com.surovtsev.utils.viewmodel.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.viewmodel.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.GameScreenEvents
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.Place
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.RankingColumn
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.RankingListHelper
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.RankingTableSortType
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.SortDirection
import com.surovtsev.cool3dminesweeper.views.glesrenderer.GLESRenderer
import com.surovtsev.touchlistener.TouchListener
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import com.surovtsev.touchlistener.dagger.TouchListenerEntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class GameScreenViewModel @Inject constructor(
    gameComponentProvider: Provider<GameComponent.Builder>,
    touchListenerComponentProvider: Provider<TouchListenerComponent.Builder>,
    savedStateHandle: SavedStateHandle
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
                val rankingTableSortType = RankingTableSortType(
                    RankingColumn.SortableColumn.DateColumn,
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
