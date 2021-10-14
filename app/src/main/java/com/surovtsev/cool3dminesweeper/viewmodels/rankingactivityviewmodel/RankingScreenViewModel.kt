package com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.dagger.app.ranking.RankingComponent
import com.surovtsev.cool3dminesweeper.dagger.app.ranking.RankingComponentEntryPoint
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
    private val rankingListHelper: RankingListHelper

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
        rankingListHelper =
            rankingComponentEntryPoint.rankingListHelper
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        loadData()
    }

    private fun loadData() {
        rankingScreenEvents.settingsDataWithIdsListData.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingScreenEvents.rankingDataListData.onDataChanged(
            let {
                val res = rankingListHelper.loadData()
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
        rankingScreenEvents.filteredRankingList.onDataChanged(
            rankingListHelper.filterData(
                rankingScreenEvents.rankingDataListData.data.value!!,
                settingsId
            )
        )
        rankingScreenEvents.selectedSettingsId.onDataChanged(settingsId)
        prepareRankingListToDisplay()

    }

    private fun prepareRankingListToDisplay() {
        val currSortType = rankingTableSortTypeData.data.value!!
        val filteredRankingList = rankingScreenEvents.filteredRankingList.data.value!!

        rankingScreenEvents.rankingListToDisplay.onDataChanged(
            rankingListHelper.sortData(
                filteredRankingList,
                currSortType
            )
        )
    }

    fun selectColumnToSortBy(
        selectedColumn: RankingColumn.SortableColumn
    ) {
        val currSortType = rankingTableSortTypeData.data.value!!
        rankingTableSortTypeData.onDataChanged(
            if (currSortType.rankingColumn != selectedColumn) {
                RankingTableSortType(
                    selectedColumn,
                    SortDirection.Descending
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
