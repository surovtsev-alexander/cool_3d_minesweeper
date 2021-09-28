package com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_model_view

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.RankingScope
import javax.inject.Inject


@RankingScope
class RankingActivityModelView @Inject constructor(
    private val settingsDBQueries: SettingsDBQueries,
    private val rankingDBQueries: RankingDBQueries,
    private val saveController: SaveController,
    private val rankingActivityEvents: RankingActivityEvents
) {
    fun loadData() {
        rankingActivityEvents.settingsListWithIds.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingActivityEvents.rankingList.onDataChanged(
            let {
                val res = rankingDBQueries.getRankingList()
                rankingActivityEvents.winsCount.onDataChanged(
                    res.map{ it.settingId }.groupingBy { it }.eachCount()
                )
                res
            }
        )

        settingsDBQueries.getId(
            saveController.loadSettingDataOrDefault()
        )?.let {
            loadRankingForSettingsId(it)
        }
    }

    fun loadRankingForSettingsId(
        settingsId: Int
    ) {
        rankingActivityEvents.rankingList.let { rl ->
            rankingActivityEvents.filteredRankingList.onDataChanged(
                rl.data.value!!.filter {
                    it.settingId == settingsId
                }
            )
            rankingActivityEvents.selectedSettingsId.onDataChanged(settingsId)
        }
    }
}
