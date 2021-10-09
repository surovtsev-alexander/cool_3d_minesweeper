package com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.dagger.app.ranking.RankingComponent
import com.surovtsev.cool3dminesweeper.dagger.app.ranking.RankingComponentEntryPoint
import com.surovtsev.cool3dminesweeper.models.game.database.RankingData
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.*
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider


@HiltViewModel
class RankingScreenViewModel @Inject constructor(
    rankingComponentProvider: Provider<RankingComponent.Builder>,
    private val saveController: SaveController
): ViewModel(), LifecycleObserver {

    private val settingsDBQueries: SettingsDBQueries
    private val rankingDBQueries: RankingDBQueries
    val rankingScreenEvents: RankingScreenEvents
    val rankingTableSortTypeData: RankingTableSortTypeData

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
        rankingScreenEvents =
            rankingComponentEntryPoint.rankingScreenEvents
        rankingTableSortTypeData =
            rankingComponentEntryPoint.rankingTableSortTypeData
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d("TEST+++", "RankingActivityViewModel onCreate")
        loadData()
    }

    private fun loadData() {
        rankingScreenEvents.settingsListWithIds.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingScreenEvents.rankingList.onDataChanged(
            let {
                val res = rankingDBQueries.getRankingList()
                rankingScreenEvents.winsCount.onDataChanged(
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
        rankingScreenEvents.rankingList.let { rl ->
            rankingScreenEvents.filteredRankingList.onDataChanged(
                rl.data.value!!.filter {
                    it.settingId == settingsId
                }
            )
            rankingScreenEvents.selectedSettingsId.onDataChanged(settingsId)
            prepareRankingListToDisplay()
        }
    }

    private fun prepareRankingListToDisplay() {
        val currSortType = rankingTableSortTypeData.data.value!!
        val filteredRankingList = rankingScreenEvents.filteredRankingList.data.value!!

        val sortingSelector = { x: RankingData ->
            when (currSortType.rankingColumn) {
                RankingColumn.SortableColumn.DateColumn -> x.dateTime
                RankingColumn.SortableColumn.SolvingTimeColumn -> x.elapsed
            }
        }
        val comparator: Comparator<RankingData> = if (currSortType.sortDirection == SortDirection.Ascending) {
            Comparator { a, b -> compareValuesBy(a, b, sortingSelector) }
        } else {
            Comparator { a, b -> compareValuesBy(b, a, sortingSelector)}
        }

        rankingScreenEvents.rankingListToDisplay.onDataChanged(
            filteredRankingList.sortedWith(
                comparator
            )
        )
    }

    fun selectSortColumn(
        selectedColumn: RankingColumn.SortableColumn
    ) {
        val currSortType = rankingTableSortTypeData.data.value!!
        rankingTableSortTypeData.onDataChanged(
            if (currSortType.rankingColumn != selectedColumn) {
                RankingTableSortType(
                    selectedColumn,
                    SortDirection.Ascending
                )
            } else {
                RankingTableSortType(
                    selectedColumn,
                    nextSortType(currSortType.sortDirection)
                )
            }
        )

        prepareRankingListToDisplay()
    }
}
