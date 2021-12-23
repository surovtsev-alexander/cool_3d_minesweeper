package com.surovtsev.gamescreen.minesweeper.helpers

import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Ranking
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.GameStatusWithElapsedFlow
import com.surovtsev.gamescreen.models.game.config.GameConfig
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatus
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatusHelper
import com.surovtsev.gamescreen.viewmodel.helpers.GameScreenEventsNames
import com.surovtsev.gamescreen.viewmodel.helpers.ShowDialogEvent
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscription
import com.surovtsev.utils.time.localdatetimehelper.LocalDateTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@GameScope
class MinesweeperGameStatusReceiver @Inject constructor(
    private val saveController: SaveController,
    private val gameConfig: GameConfig,
    private val settingsDao: SettingsDao,
    private val rankingDao: RankingDao,
    private val gameStatusWithElapsedFlow: GameStatusWithElapsedFlow,
    @Named(GameScreenEventsNames.ShowDialog)
    private val showDialog: ShowDialogEvent,
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

    private suspend fun gameStatusUpdated(
        newStatus: GameStatus,
        elapsed: Long
    ) {
        if (!GameStatusHelper.isGameOver(newStatus)) {
            return
        }

        saveController.emptyData(
            SaveTypes.SaveGameJson
        )

        do {
            if (newStatus != GameStatus.Win) {
                break
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
        } while (false)

        withContext(Dispatchers.Main) {
            showDialog.onDataChanged(true)
        }
    }
}