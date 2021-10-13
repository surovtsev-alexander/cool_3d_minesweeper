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

    @SuppressWarnings("UNUSED")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
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
            val filteredData = rl.data.value!!.filter {
                it.settingId == settingsId
            }
            val rankingListWithPlaces = RankingListWithPlacesHelper.create(filteredData)
            rankingScreenEvents.filteredRankingList.onDataChanged(
                rankingListWithPlaces
            )
            rankingScreenEvents.selectedSettingsId.onDataChanged(settingsId)
            prepareRankingListToDisplay()
        }
    }

    private fun prepareRankingListToDisplay() {
        val currSortType = rankingTableSortTypeData.data.value!!
        val filteredRankingList = rankingScreenEvents.filteredRankingList.data.value!!

        val sortingSelector = { x: RankingDataWithPlaces ->
            when (currSortType.rankingColumn) {
                RankingColumn.SortableColumn.DateColumn -> x.rankingData.dateTime
                RankingColumn.SortableColumn.SolvingTimeColumn -> x.rankingData.elapsed
            }
        }
        val comparator: Comparator<RankingDataWithPlaces> = if (currSortType.sortDirection == SortDirection.Ascending) {
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
