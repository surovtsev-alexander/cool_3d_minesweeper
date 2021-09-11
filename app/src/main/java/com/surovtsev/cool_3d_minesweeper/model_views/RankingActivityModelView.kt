package com.surovtsev.cool_3d_minesweeper.model_views

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData

class RankingActivityModelView {
    val settingsList = MyLiveData(
        listOf<DataWithId<SettingsData>>()
    )
    val filteredRankingList = MyLiveData(
        listOf<RankingData>()
    )
    val selectedSettingsId = MyLiveData(-1)


    private val applicationController = ApplicationController.getInstance()
    private val settingsDBQueries = applicationController.settingsDBQueries.value
    private val rankingDBQueries = applicationController.rankingDBQueries.value

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
            applicationController.saveController.loadSettingDataOrDefault()
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
