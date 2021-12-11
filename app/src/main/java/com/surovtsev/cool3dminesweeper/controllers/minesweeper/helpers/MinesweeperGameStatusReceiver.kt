package com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.gameinteraction.GameStatusReceiver
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatus
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Ranking
import com.surovtsev.cool3dminesweeper.utils.time.localdatetimehelper.LocalDateTimeHelper
import com.surovtsev.game.dagger.GameScope
import javax.inject.Inject

@GameScope
class MinesweeperGameStatusReceiver @Inject constructor(
    private val saveController: SaveController,
    private val gameConfig: GameConfig,
    private val settingsDao: SettingsDao,
    private val rankingDao: RankingDao,
): GameStatusReceiver {
    override fun gameStatusUpdated(
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