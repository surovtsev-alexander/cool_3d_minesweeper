package com.surovtsev.cool_3d_minesweeper.model_views

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.RankingData
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.live_data.MyLiveData

class RankingActivityModelView {
    val settingsList = MyLiveData<List<DataWithId<SettingsData>>>(
        listOf<DataWithId<SettingsData>>()
    )
    val filteredRankingList = MyLiveData<List<RankingData>>(
        listOf<RankingData>()
    )
    val selectedSettingsId = MyLiveData<Int>(-1)


    val applicationController = ApplicationController.getInstance()
    val settingsDBQueries = applicationController.settingsDBQueries
    val rankingDBQueries = applicationController.rankingDBQueries

    var rankingList: List<RankingData>? = null
        private set
    var winsCount: Map<Int, Int>? = null
        private set

    fun loadData() {
        settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingList = rankingDBQueries.getRankingList()
        winsCount = rankingList?.map{ it.settingId }?.groupingBy { it }?.eachCount()
    }

    fun loadRankingForSettingsId(
        settingsId: Int
    ) {
        rankingList?.let {
            filteredRankingList.onDataChanged(
                it.filter {
                    it.settingId == settingsId
                }
            )
            selectedSettingsId.onDataChanged(settingsId)
        }
    }
}
