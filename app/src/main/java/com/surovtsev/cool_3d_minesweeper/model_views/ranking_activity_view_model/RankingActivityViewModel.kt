package com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.ranking.RankingComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.ranking.RankingComponentEntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider


@HiltViewModel
class RankingActivityViewModel @Inject constructor(
    rankingComponentProvider: Provider<RankingComponent.Builder>,
    private val saveController: SaveController
): ViewModel(), LifecycleObserver {

    private val settingsDBQueries: SettingsDBQueries
    private val rankingDBQueries: RankingDBQueries
    val rankingActivityEvents: RankingActivityEvents

    init {
        val rankingComponent = rankingComponentProvider
            .get()
            .build()
        val rankingComponentEntryPoint =
            EntryPoints.get(
                rankingComponent,
                RankingComponentEntryPoint::class.java
            )

        settingsDBQueries =
            rankingComponentEntryPoint.settingsDBQueries
        rankingDBQueries =
            rankingComponentEntryPoint.rankingDBQueries
        rankingActivityEvents =
            rankingComponentEntryPoint.rankingActivityEvents
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d("TEST+++", "RankingActivityViewModel onCreate")
        loadData()
    }

    private fun loadData() {
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
