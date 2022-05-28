package com.surovtsev.gamelogic.minesweeper.helpers

import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.helpers.sorting.RankingTableColumn
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.helpers.sorting.SortDirection
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Ranking
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.GameStatusHolderBridge
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatus
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusHelper
import com.surovtsev.utils.coroutines.customcoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.time.localdatetimehelper.LocalDateTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@GameScope
class MinesweeperGameStatusReceiver @Inject constructor(
    private val saveController: SaveController,
    private val gameStatusHolderBridge: GameStatusHolderBridge,
    private val settingsDao: SettingsDao,
    private val rankingDao: RankingDao,
    private val uiGameControlsMutableFlows: UIGameControlsMutableFlows,
    private val rankingListHelper: RankingListHelper,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {

    init {
        subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        restartableCoroutineScope.launch {
            gameStatusHolderBridge.gameStatusWithElapsedFlow.collectLatest {
                it?.let {
                    gameStatusUpdated(
                        it.gameConfig,
                        it.gameStatus,
                        it.elapsed
                    )
                }
            }
        }
    }

    private suspend fun gameStatusUpdated(
        gameConfig: GameConfig,
        newStatus: GameStatus,
        elapsed: Long
    ) {
        if (!GameStatusHelper.isGameOver(newStatus)) {
            return
        }

        saveController.emptyData(
            SaveTypes.SaveGameJson
        )

        val newUIGameStatus: UIGameStatus
        if (newStatus == GameStatus.Lose) {
            newUIGameStatus = UIGameStatus.Lose
        } else {
            val settings = settingsDao.getOrCreate(
                gameConfig.settingsData,
            )

            val rankingData = Ranking.RankingData(
                settings.id,
                elapsed,
                LocalDateTimeHelper.epochMilli
            )
            rankingDao.insert(
                Ranking(
                    rankingData
                )
            )

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

            /* place is counted from 0 */
            val winPlace = sortedData.first().place + 1

            newUIGameStatus = UIGameStatus.Win(winPlace, elapsed)
        }

        withContext(Dispatchers.Main) {
            uiGameControlsMutableFlows.uiGameStatus.value = newUIGameStatus
        }
    }
}