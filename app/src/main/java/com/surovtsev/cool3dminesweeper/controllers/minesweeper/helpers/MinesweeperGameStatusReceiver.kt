package com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.gameinteraction.GameStatusReceiver
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveTypes
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatus
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.models.room.entities.Ranking
import com.surovtsev.cool3dminesweeper.utils.time.localdatetimehelper.LocalDateTimeHelper
import logcat.logcat
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