package com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_model_view

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.AppScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject

typealias SettingsListWithIds = MyLiveData<List<DataWithId<SettingsData>>>
typealias RankingList = MyLiveData<List<RankingData>>
typealias SelectedSettingsId = MyLiveData<Int>

@AppScope
class RankingActivityModelView @Inject constructor(
    private val settingsDBQueries: SettingsDBQueries,
    private val rankingDBQueries: RankingDBQueries,
    private val saveController: SaveController
) {
    val settingsList: SettingsListWithIds = MyLiveData(
        listOf<DataWithId<SettingsData>>()
    )
    val filteredRankingList: RankingList = MyLiveData(
        listOf<RankingData>()
    )
    val selectedSettingsId = MyLiveData(-1)

    private var rankingList: List<RankingData>? = null
    var winsCount: Map<Int, Int>? = null
        private set

    fun loadData() {
        settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingList = rankingDBQueries.getRankingList()
        winsCount = rankingList?.map{ it.settingId }?.groupingBy { it }?.eachCount()

        settingsDBQueries.getId(
            saveController.loadSettingDataOrDefault()
        )?.let {
            loadRankingForSettingsId(it)
        }
    }

    fun loadRankingForSettingsId(
        settingsId: Int
    ) {
        rankingList?.let { rl ->
            filteredRankingList.onDataChanged(
                rl.filter {
                    it.settingId == settingsId
                }
            )
            selectedSettingsId.onDataChanged(settingsId)
        }
    }
}
