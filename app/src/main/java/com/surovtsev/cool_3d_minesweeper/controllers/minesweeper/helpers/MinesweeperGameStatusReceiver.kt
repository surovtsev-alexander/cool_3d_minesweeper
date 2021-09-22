package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.GameLogicStateHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameStatusReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@GameControllerScope
class MinesweeperGameStatusReceiver @Inject constructor(
    private val context: Context,
    private val saveController: SaveController,
    private val gameConfig: GameConfig
): IGameStatusReceiver {
    override fun gameStatusUpdated(
        newStatus: GameStatus,
        elapsed: Long
    ) {
        if (newStatus == GameStatus.WIN ||
            newStatus == GameStatus.LOSE) {
            saveController.emptyData(
                SaveTypes.SaveGameJson
            )
        }

        if (newStatus != GameStatus.WIN) {
            return
        }

        val dbHelper = DBHelper(context)
        val settingsDBHelper = SettingsDBQueries(dbHelper)
        val rankingDBQueries = RankingDBQueries(dbHelper)

        val settingId = settingsDBHelper.insertIfNotPresent(gameConfig.settingsData)
        val rankingData = RankingData(
            settingId,
            elapsed,
            LocalDateTime.now().toString()
        )
        rankingDBQueries.insert(rankingData)
    }
}