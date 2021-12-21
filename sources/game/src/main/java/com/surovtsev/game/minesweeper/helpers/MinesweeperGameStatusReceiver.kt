package com.surovtsev.game.minesweeper.helpers

import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Ranking
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.minesweeper.gamelogic.helpers.GameStatusWithElapsedFlow
import com.surovtsev.game.models.game.config.GameConfig
import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.coroutines.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.subscriptions.Subscription
import com.surovtsev.utils.time.localdatetimehelper.LocalDateTimeHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@GameScope
class MinesweeperGameStatusReceiver @Inject constructor(
    private val saveController: SaveController,
    private val gameConfig: GameConfig,
    private val settingsDao: SettingsDao,
    private val rankingDao: RankingDao,
    private val gameStatusWithElapsedFlow: GameStatusWithElapsedFlow,
    subscriber: Subscriber
): Subscription {

    init {
        subscriber.addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            gameStatusWithElapsedFlow.collectLatest {
                gameStatusUpdated(
                    it.gameStatus,
                    it.elapsed
                )
            }
        }
    }

    private fun gameStatusUpdated(
        newStatus: GameStatus,
        elapsed: Long
    ) {
        if (newStatus == GameStatus.Win ||
            newStatus == GameStatus.Lose) {
            saveController.emptyData(
                SaveTypes.SaveGameJson
            )
        }

        if (newStatus != GameStatus.Win) {
            return
        }

        val settings = settingsDao.getOrCreate(
            gameConfig.settingsData
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
    }
}